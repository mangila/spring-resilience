package com.github.mangila.resilience.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class OrderControllerTest {

    @Autowired
    private WebTestClient http;

    @Test
    void createNewOrder() {
        http.post()
                .uri("/api/v1/order")
                .bodyValue(new CreateNewOrderRequest(
                        "123",
                        List.of(),
                        "Street 123"))
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }

    @Test
    void findOrder() {
    }
}