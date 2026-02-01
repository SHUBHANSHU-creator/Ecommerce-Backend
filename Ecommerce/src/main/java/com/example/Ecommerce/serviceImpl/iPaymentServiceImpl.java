package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class iPaymentServiceImpl {
    @Autowired
    private CardServiceImpl cardService;

    @Autowired
    private UPIPaymentServiceImpl upiPaymentService;

    public void selectPaymentService(PaymentMethod paymentMethod, Order order) {
        if (paymentMethod == PaymentMethod.UPI) {
            upiPaymentService.pay(order);
        } else if (paymentMethod == PaymentMethod.CARD) {
            cardService.pay(order);
        }
    }
}
