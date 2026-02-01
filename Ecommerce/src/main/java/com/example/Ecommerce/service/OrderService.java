package com.example.Ecommerce.service;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
     List<Order> getAllOrders();
     Order getOrderById(Long id);
     String placeOrder(Order order);
     String cancelOrder(Long id);
     Order updateOrderStatus(Long orderId, OrderStatus status);
}
