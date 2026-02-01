package com.example.Ecommerce.service;

import com.example.Ecommerce.Entity.Inventory;
import org.springframework.stereotype.Service;

@Service
public interface InventoryService {
    boolean checkAvailability(Long productId, Integer quantity);
    void reduceStock(Long productId, Integer quantity);
    void restockProduct(Long productId, Integer quantity);
    Inventory addNewProduct(Long productId, Integer quantity);
}
