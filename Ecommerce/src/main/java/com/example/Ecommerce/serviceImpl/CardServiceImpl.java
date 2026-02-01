package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements PaymentService {

    @Transactional
    @Override
    public void pay(Order order) {
        System.out.println("Paid Using CreditCard");
    }
}
