package com.jwtauth.jwtauth.handler;


import com.jwtauth.jwtauth.entity.OrderEntity;
import com.jwtauth.jwtauth.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderHandler {

    private final OrderRepository orderRepository;

    public OrderHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

}
