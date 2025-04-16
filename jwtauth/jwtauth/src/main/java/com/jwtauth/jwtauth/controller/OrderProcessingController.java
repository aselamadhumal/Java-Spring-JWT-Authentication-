package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.model.OrderEntity;
import com.jwtauth.jwtauth.service.OrderProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderProcessingController {


    private final OrderProcessingService orderProcessingService;

    public OrderProcessingController(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderEntity order) {
        return ResponseEntity.ok(orderProcessingService.placeOrder(order));
    }

}
