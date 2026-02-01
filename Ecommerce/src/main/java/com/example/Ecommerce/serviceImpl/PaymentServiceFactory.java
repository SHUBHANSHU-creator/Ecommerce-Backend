package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.exception.InvalidOrderException;
import com.example.Ecommerce.response.ApiResponse;
import com.example.Ecommerce.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceFactory {
    private final Map<PaymentMethod, PaymentService> paymentServices;


    //It performs autowiring by type. No need to create constructor and initalize with services yourself
    //since my specific services are implementing payment service they will get autowired
    public PaymentServiceFactory(List<PaymentService> paymentServices) {
        Map<PaymentMethod, PaymentService> resolvedServices = new EnumMap<>(PaymentMethod.class);
        for (PaymentService paymentService : paymentServices) {
            resolvedServices.put(paymentService.getPaymentMethod(), paymentService);
        }
        this.paymentServices = resolvedServices;
    }

    public PaymentService resolve(PaymentMethod paymentMethod) {
        PaymentService paymentService = paymentServices.get(paymentMethod);
        if (paymentService == null) {
            throw new InvalidOrderException("Unsupported payment method: " + paymentMethod);
        }
        return paymentService;
    }

    public ApiResponse<String> processPayment(PaymentMethod paymentMethod, Order order) {
        return resolve(paymentMethod).pay(order);
    }
}
