package com.github.mangila.resilience.orderservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createNewOrder(@RequestBody CreateNewOrderRequest request) {
        log.info("Creating new order {}", request);
        String id = orderService.createNewOrder(request);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> findOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.findById(orderId));
    }

}
