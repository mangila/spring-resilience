package com.github.mangila.resilience.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
@Slf4j
public class DeliveryServiceClient {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RestClient restClient;
    private final String basePath;

    public DeliveryServiceClient(ObjectMapper objectMapper,
                                 @Qualifier("deliveryRestClient") RestClient restClient,
                                 @Value("${application.integration.delivery-service.basepath}") String basePath,
                                 RabbitTemplate rabbitTemplate) {
        Assert.hasText(basePath, "`application.integration.delivery-service.basepath` must be set");
        this.objectMapper = objectMapper;
        this.restClient = restClient;
        this.basePath = basePath;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Retry(name = "rabbit-mq-retry",
            fallbackMethod = "enqueueNewOrderFallback")
    @CircuitBreaker(name = "rabbit-mq-circuit-breaker")
    public void enqueueNewOrder(ObjectNode objectNode) {
        rabbitTemplate.convertAndSend(Config.NEW_ORDER_TO_DELIVERY_QUEUE, objectNode.toString());
    }

    public void enqueueNewOrderFallback(ObjectNode objectNode, Throwable exception) {
        log.error("Failed to enqueue new order: {}", objectNode, exception);
        if (Objects.requireNonNull(exception) instanceof AmqpConnectException ace) {
            // handle connection error
            log.error("RabbitMQ connection error: {}", ace.getMessage());
        } else {
            log.error("error: {}", exception.getMessage());
            // queue DLQ or retry
        }
    }

    @Retry(
            name = "deliveryService",
            fallbackMethod = "getDeliveryFallback")
    @CircuitBreaker(name = "deliveryService")
    public ObjectNode getDelivery(String orderId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(basePath.concat("/{orderId}"))
                        .build(orderId))
                .retrieve()
                .body(ObjectNode.class);
    }

    public ObjectNode getDeliveryFallback(String orderId, Throwable throwable) {
        log.error("Failed to get delivery for orderId: {}", orderId, throwable);
        return objectMapper.createObjectNode();
    }
}
