package com.microservices.order.service;


import com.microservices.order.entity.Order;
import com.microservices.order.exception.OrderCustomException;
import com.microservices.order.external.client.ProductClient;
import com.microservices.order.external.response.ProductResponse;
import com.microservices.order.model.OrderRequest;
import com.microservices.order.model.OrderResponse;
import com.microservices.order.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.Callable;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired private OrderRepository orderRepository;
  @Autowired ProductClient productClient;
  @Autowired RestTemplate restTemplate;
  ResponseEntity<?> product = null;


  @Override
  public ResponseEntity<?> placeOrder(OrderRequest orderRequest) {

    try {
      product =
          productClient.reduceProduct(orderRequest.getProductId(), orderRequest.getQuantity());
    } catch (OrderCustomException e) {
      return new ResponseEntity<>(e.toString(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Order order =
        Order.builder()
            .orderDate(Instant.now())
            .amount(orderRequest.getTotalAmount())
            .quantity(orderRequest.getQuantity())
            .productId(orderRequest.getProductId())
            .orderStatus("CREATED")
            .build();
    order = orderRepository.save(order);
    if (order == null) {
      throw new OrderCustomException()
          .builder()
          .errorMessage("Order didn't placed")
          .errorCode(HttpStatus.NOT_FOUND.value())
          .errorDescription("ORDER_NOT_PLACED")
          .build();
    }
    return new ResponseEntity<>("succ", HttpStatus.ACCEPTED);
  }

  @Override
  public OrderResponse getOrderDetails(long orderId) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> {
                  throw new OrderCustomException()
                      .builder()
                      .errorDescription("THe Order doesn't exist")
                      .errorCode(HttpStatus.NOT_FOUND.value())
                      .errorMessage("ORDER_DOESN'T_EXIST")
                      .build();
                });
    ProductResponse product =
        restTemplate.getForObject(
            "http://localhost:8080/product/" + order.getProductId(), ProductResponse.class);

    OrderResponse.ProductDetails productDetails =
        OrderResponse.ProductDetails.builder()
            .productId(product.getProductId())
            .quantity(product.getQuantity())
            .productName(product.getProductName())
            .price(product.getPrice())
            .build();
    OrderResponse orderResponse =
        new OrderResponse()
            .builder()
            .orderId(order.getId())
            .orderDate(order.getOrderDate())
            .orderStatus(order.getOrderStatus())
            .amount(order.getAmount())
            .productDetails(productDetails)
            .build();

    return orderResponse;
  }
}
