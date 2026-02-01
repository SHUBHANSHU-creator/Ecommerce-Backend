package com.example.Ecommerce.service;

import com.example.Ecommerce.Entity.Inventory;
import com.example.Ecommerce.Entity.Product;
import com.example.Ecommerce.response.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface InventoryService {
    ApiResponse<Boolean> checkAvailability(Long productId, Integer quantity);
    ApiResponse<Inventory> reduceStock(Long productId, Integer quantity);
    ApiResponse<Inventory> restockProduct(Long productId, Integer quantity);
    ApiResponse<Inventory> addNewProduct(Product product, Integer quantity);
}
