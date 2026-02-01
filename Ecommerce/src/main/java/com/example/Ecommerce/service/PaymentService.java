package com.example.Ecommerce.service;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.PaymentMethod;
import org.springframework.stereotype.Service;


public interface PaymentService {
    PaymentMethod getPaymentMethod();
    void pay(Order order);
}
