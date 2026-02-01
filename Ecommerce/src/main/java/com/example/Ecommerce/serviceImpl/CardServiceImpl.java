package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements PaymentService {

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CARD;
    }

    @Transactional
    @Override
    public void pay(Order order) {
        System.out.println("Paid Using CreditCard");
    }
}
