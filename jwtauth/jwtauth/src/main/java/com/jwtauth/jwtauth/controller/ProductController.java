package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.model.Product2Entity;
import com.jwtauth.jwtauth.repository.Product2Repository;
import com.jwtauth.jwtauth.service.GenericCrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final GenericCrudService<Product2Entity, Long> service;

    public ProductController(Product2Repository productRepository) {
        this.service = new GenericCrudService<>(productRepository);
    }

    @PostMapping
    public ResponseEntity<Product2Entity> create(@RequestBody Product2Entity product) {
        Product2Entity createdProduct = service.save(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product2Entity>> getAll() {
        List<Product2Entity> products = service.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product2Entity> getById(@PathVariable Long id) {
        Optional<Product2Entity> product = service.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product2Entity> update(@PathVariable Long id, @RequestBody Product2Entity updatedProduct) {
        try {
            Product2Entity updated = service.update(id, updatedProduct);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
