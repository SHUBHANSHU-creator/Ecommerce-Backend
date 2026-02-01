package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.response.ApiResponse;
import com.example.Ecommerce.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UPIPaymentServiceImpl implements PaymentService {

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.UPI;
    }

    @Override
    public ApiResponse<String> pay(Order order) {
        return new ApiResponse<>(HttpStatus.OK.value(), "Paid using UPI", "Paid Using UPI");
    }
}
