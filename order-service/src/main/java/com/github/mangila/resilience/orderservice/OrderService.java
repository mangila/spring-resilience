package com.github.mangila.resilience.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
public class OrderService {
    private final DeliveryServiceClient deliveryServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final MemoryDatabase memoryDatabase;
    private final ObjectMapper objectMapper;

    public OrderService(DeliveryServiceClient deliveryServiceClient,
                        RabbitTemplate rabbitTemplate,
                        MemoryDatabase memoryDatabase,
                        ObjectMapper objectMapper) {
        this.deliveryServiceClient = deliveryServiceClient;
        this.rabbitTemplate = rabbitTemplate;
        this.memoryDatabase = memoryDatabase;
        this.objectMapper = objectMapper;
    }

    public String createNewOrder(CreateNewOrderRequest request) {
        log.trace("Order create");
        String id = UUID.randomUUID().toString();
        Order order = new Order(id,
                request.customerId(),
                request.items(),
                2.0);
        log.trace("Order enqueue to delivery service");
        rabbitTemplate.convertAndSend(Config.NEW_ORDER_TO_DELIVERY_QUEUE, objectMapper.createObjectNode()
                .put("id", order.id())
                .put("address", request.address())
                .toString());
        log.trace("Order save to database");
        memoryDatabase.save(order);
        log.trace("Order created with id {}", id);
        return id;
    }

    public OrderDto findById(String orderId) {
        log.trace("Getting order with orderId {}", orderId);
        return Stream.of(memoryDatabase.findById(orderId))
                .filter(Objects::nonNull)
                .peek(order -> log.trace("Order found {}", order))
                .map(order -> {
                    ObjectNode json = deliveryServiceClient.getDelivery(order.id());
                    return new OrderDto(
                            order.customerId(),
                            order.items(),
                            json.get("address").asText(),
                            json.get("status").asText(),
                            order.price()
                    );
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
}
