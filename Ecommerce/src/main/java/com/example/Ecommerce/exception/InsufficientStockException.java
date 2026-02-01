package com.example.Ecommerce.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, Integer requested, Integer available) {
        super("Insufficient stock for productId=" + productId + ". Requested=" + requested + ", available=" + available);
    }
}
