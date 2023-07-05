package com.microservices.order.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.order.model.OrderRequest;
import com.microservices.order.model.OrderResponse;
import com.microservices.order.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest)  {
        return orderService.placeOrder(orderRequest);
//        log.info("Order Id: {}", orderId);
//        return new ResponseEntity<>(orderId, HttpStatus.OK);
//        long orderId = orderService.placeOrder(orderRequest);
//        log.info("Order Id: {}", orderId);
//        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long orderId) {
        OrderResponse orderResponse
                = orderService.getOrderDetails(orderId);

        return new ResponseEntity<>(orderResponse,
                HttpStatus.OK);
    }
}