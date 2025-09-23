package com.github.mangila.resilience.orderservice;

import java.util.List;

public record CreateNewOrderRequest(
        String customerId,
        List<String> items,
        String address
) {
}
