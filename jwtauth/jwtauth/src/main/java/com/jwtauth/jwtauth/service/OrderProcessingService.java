package com.jwtauth.jwtauth.service;

import org.springframework.stereotype.Service;
import com.jwtauth.jwtauth.handler.InventoryHandler;
import com.jwtauth.jwtauth.handler.OrderHandler;
import com.jwtauth.jwtauth.model.OrderEntity;
import com.jwtauth.jwtauth.model.ProductEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service  // <-- Add this annotation
public class OrderProcessingService {

    private final OrderHandler orderHandler;
    private final InventoryHandler inventoryHandler;

    public OrderProcessingService(OrderHandler orderHandler, InventoryHandler inventoryHandler) {
        this.orderHandler = orderHandler;
        this.inventoryHandler = inventoryHandler;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public OrderEntity placeOrder(OrderEntity order) {
        // get the product from inventory
        ProductEntity product = inventoryHandler.getProduct(order.getProductId());

        // validate stock availability
        validateStockAvailability(order, product);

        // update total price in order entity
        order.setTotalPrice(order.getQuantity() * product.getPrice());

        // save order
        OrderEntity saveOrder = orderHandler.saveOrder(order);

        // update stock in inventory
        updateInventoryStock(order, product);

        return saveOrder;
    }


    private static void validateStockAvailability(OrderEntity order, ProductEntity product) {
        // Check if the order quantity is greater than the available stock
        if (order.getQuantity() > product.getStockQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }
    }

    private void updateInventoryStock(OrderEntity order, ProductEntity product) {
        int availableStock = product.getStockQuantity() - order.getQuantity();
        product.setStockQuantity(availableStock);
        inventoryHandler.updateProductDetails(product);
    }
}
