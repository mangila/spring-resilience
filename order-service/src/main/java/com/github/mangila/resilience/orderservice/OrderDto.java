package com.github.mangila.resilience.orderservice;

import java.util.List;

public record OrderDto(
        String customerId,
        List<String> items,
        String address,
        String status,
        double price
) {
}
