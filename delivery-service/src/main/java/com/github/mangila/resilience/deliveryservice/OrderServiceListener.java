package com.github.mangila.resilience.deliveryservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceListener {

    private final MemoryDatabase database;
    private final ObjectMapper objectMapper;

    public OrderServiceListener(MemoryDatabase database,
                                ObjectMapper objectMapper) {
        this.database = database;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
            queues = "new-order-to-delivery-queue",
            containerFactory = "rabbitListenerContainerFactory",
            executor = "rabbitListenerExecutor"
    )
    public void listen(String s) {
        log.info("Received message: {}", s);
        try {
            ObjectNode jsonObj = (ObjectNode) objectMapper.readTree(s);
            String orderId = jsonObj.get("id").asText();
            String address = jsonObj.get("address").asText();
            log.trace("Order Message: {} - {}", orderId, address);
            Delivery delivery = new Delivery(orderId, address, "PENDING");
            log.trace("Order saved to database: {}", delivery);
            database.save(delivery);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
