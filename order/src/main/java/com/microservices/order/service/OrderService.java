package com.microservices.order.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.order.model.OrderRequest;
import com.microservices.order.model.OrderResponse;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<?> placeOrder(OrderRequest orderRequest) ;

    OrderResponse getOrderDetails(long orderId);
}
