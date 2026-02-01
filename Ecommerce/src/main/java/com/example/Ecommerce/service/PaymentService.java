package com.example.Ecommerce.service;

import com.example.Ecommerce.Entity.Order;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    void pay(Order order);
}
