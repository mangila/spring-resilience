package com.github.mangila.resilience.orderservice;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.NamingConvention;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

@Configuration
public class Config {

    public static final String NEW_ORDER_TO_DELIVERY_QUEUE = "new-order-to-delivery-queue";

    @Bean
    public Queue createNewOrderToDeliveryQueue() {
        return new Queue(NEW_ORDER_TO_DELIVERY_QUEUE, Boolean.FALSE);
    }

    @Bean
    RestClient deliveryRestClient(
            @Value("${spring.application.name}") String applicationName,
            @Value("${application.integration.delivery-service.url}") String url) {
        Assert.hasText(applicationName, "`spring.application.name` must be set");
        Assert.hasText(url, "`application.integration.delivery-service.url` must be set");
        return RestClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, applicationName)
                .build();
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> customizer() {
        return registry -> {
            registry.config().namingConvention(NamingConvention.snakeCase);
        };
    }
}
