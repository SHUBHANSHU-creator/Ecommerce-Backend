package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Entity.OrderItem;
import com.example.Ecommerce.Entity.Payments;
import com.example.Ecommerce.Entity.Product;
import com.example.Ecommerce.Enums.OrderStatus;
import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.Repository.OrderRepository;
import com.example.Ecommerce.exception.InvalidOrderException;
import com.example.Ecommerce.exception.OrderNotFoundException;
import com.example.Ecommerce.response.ApiResponse;
import com.example.Ecommerce.service.InventoryService;
import com.example.Ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PaymentServiceFactory paymentServiceFactory;

    @Override
    public ApiResponse<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return new ApiResponse<>(HttpStatus.OK.value(), "Orders retrieved successfully", orders);
    }


    @Override
    public ApiResponse<Order> getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return new ApiResponse<>(HttpStatus.OK.value(), "Order retrieved successfully", order);
    }

    @Override
    @Transactional
    public ApiResponse<Order> placeOrder(Order order) {
        if (order == null) {
            throw new InvalidOrderException("Order payload must be provided");
        }
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }
        //check availability
        //reduce stock
        //calculate total if not done yet

        double totalAmount = 0;
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            if (product == null) {
                throw new InvalidOrderException("Order item must reference a product");
            }
            if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
                throw new InvalidOrderException("Order item must contain at least one quantity");
            }
            ApiResponse<Boolean> availabilityResponse = inventoryService.checkAvailability(product.getId(), orderItem.getQuantity());
            if (!Boolean.TRUE.equals(availabilityResponse.getData())) {
                throw new InvalidOrderException("Insufficient stock for productId=" + product.getId());
            }
            inventoryService.reduceStock(product.getId(), orderItem.getQuantity());
            orderItem.setOrder(order);
            totalAmount += orderItem.getPriceAtPurchase() * orderItem.getQuantity();
        }

        if(order.getTotalAmount() == null){
            order.setTotalAmount(totalAmount);
        }
        //set order status to placed
        order.setOrderStatus(OrderStatus.PLACED);
        Payments payments = order.getPayments();
        if(payments != null && payments.getPaymentMethod() != null && payments.getPaymentMethod() != PaymentMethod.COD) {
            paymentServiceFactory.processPayment(payments.getPaymentMethod(),order);
        }
        Order savedOrder = orderRepository.save(order);
        return new ApiResponse<>(HttpStatus.CREATED.value(), "Order placed successfully", savedOrder);
    }


    @Override
    public ApiResponse<Order> cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getOrderStatus() != OrderStatus.PLACED) {
            return new ApiResponse<>(HttpStatus.CONFLICT.value(), "Order cannot be cancelled", order);
        }
        order.setOrderStatus(OrderStatus.CANCELED);
        Order savedOrder = orderRepository.save(order);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order cancelled and stock restored", savedOrder);
    }

    @Override
    public ApiResponse<Order> updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (status == null) {
            throw new InvalidOrderException("Order status must be provided");
        }
        order.setOrderStatus(status);
        Order savedOrder = orderRepository.save(order);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order status updated successfully", savedOrder);
    }
}
