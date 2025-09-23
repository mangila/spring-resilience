package com.github.mangila.resilience.orderservice;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

@Service
public class DeliveryServiceClient {

    private final RestClient restClient;
    private final String basePath;


    public DeliveryServiceClient(@Qualifier("deliveryRestClient") RestClient restClient,
                                 @Value("${application.integration.delivery-service.basepath}") String basePath) {
        Assert.hasText(basePath, "`application.integration.delivery-service.basepath` must be set");
        this.restClient = restClient;
        this.basePath = basePath;
    }

    public ObjectNode getDelivery(String orderId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(basePath.concat("/{orderId}"))
                        .build(orderId))
                .retrieve()
                .body(ObjectNode.class);
    }
}
