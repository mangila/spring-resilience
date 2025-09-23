package com.github.mangila.resilience.deliveryservice;

public record Delivery(String orderId,
                       String address,
                       String status) {
}
