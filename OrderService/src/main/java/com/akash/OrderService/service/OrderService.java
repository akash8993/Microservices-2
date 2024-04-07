package com.akash.OrderService.service;

import com.akash.OrderService.model.OrderRequest;
import com.akash.OrderService.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
