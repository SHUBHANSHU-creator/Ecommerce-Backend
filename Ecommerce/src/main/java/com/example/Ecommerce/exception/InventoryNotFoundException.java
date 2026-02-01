package com.example.Ecommerce.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Long productId) {
        super("Inventory record not found for productId=" + productId);
    }
}
