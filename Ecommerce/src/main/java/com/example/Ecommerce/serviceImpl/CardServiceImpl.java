package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.response.ApiResponse;
import com.example.Ecommerce.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements PaymentService {

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CARD;
    }

    @Transactional
    @Override
    public ApiResponse<String> pay(Order order) {
        return new ApiResponse<>(HttpStatus.OK.value(), "Paid using credit card", "Paid Using CreditCard");
    }
}
