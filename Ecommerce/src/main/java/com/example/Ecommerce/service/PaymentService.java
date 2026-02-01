package com.example.Ecommerce.service;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.response.ApiResponse;
import org.springframework.stereotype.Service;


public interface PaymentService {
    PaymentMethod getPaymentMethod();
    ApiResponse<String> pay(Order order);
}
