package com.jwtauth.jwtauth.handler;


import com.jwtauth.jwtauth.entity.ProductEntity;
import com.jwtauth.jwtauth.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryHandler {

    private final InventoryRepository inventoryRepository;

    public InventoryHandler(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public ProductEntity updateProductDetails(ProductEntity productEntity) {

        //forcefully throwing exception to simulate use of tx
        if(productEntity.getPrice() > 5000){
            throw new RuntimeException("DB crashed.....");
        }

        return inventoryRepository.save(productEntity);
    }

    public ProductEntity getProduct(int id){
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
