package com.example.Ecommerce.service;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.OrderStatus;
import com.example.Ecommerce.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
     ApiResponse<List<Order>> getAllOrders();
     ApiResponse<Order> getOrderById(Long id);
     ApiResponse<Order> placeOrder(Order order);
     ApiResponse<Order> cancelOrder(Long id);
     ApiResponse<Order> updateOrderStatus(Long orderId, OrderStatus status);
}
