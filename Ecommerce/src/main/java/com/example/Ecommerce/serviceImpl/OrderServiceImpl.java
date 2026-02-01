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
import com.example.Ecommerce.service.InventoryService;
import com.example.Ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    @Transactional
    public String placeOrder(Order order) {
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
            if (!inventoryService.checkAvailability(product.getId(), orderItem.getQuantity())) {
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
        orderRepository.save(order);
        return"Order placed successfully";
    }


    @Override
    public String cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getOrderStatus() != OrderStatus.PLACED) {
            return "Order cannot be cancelled";
        }
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        return "Order cancelled and stock restored";
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (status == null) {
            throw new InvalidOrderException("Order status must be provided");
        }
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }
}
