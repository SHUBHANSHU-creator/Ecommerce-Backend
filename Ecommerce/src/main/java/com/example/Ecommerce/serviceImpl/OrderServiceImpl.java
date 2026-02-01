package com.example.Ecommerce.serviceImpl;

import com.example.Ecommerce.Entity.Order;
import com.example.Ecommerce.Entity.OrderItem;
import com.example.Ecommerce.Entity.Product;
import com.example.Ecommerce.Enums.OrderStatus;
import com.example.Ecommerce.Repository.OrderRepository;
import com.example.Ecommerce.service.InventoryService;
import com.example.Ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order Not Found"));
    }

    @Override
    public String placeOrder(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        //check availability
        //reduce stock
        //calculate total if not done yet

        double totalAmount = 0;
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            if (product == null) {
                throw new IllegalArgumentException("Order item must reference a product");
            }
            if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
                throw new IllegalArgumentException("Order item must contain at least one quantity");
            }
            if (!inventoryService.checkAvailability(product.getProductId(), orderItem.getQuantity())) {
                throw new IllegalArgumentException("Order item quantity is not enough");
            }
            inventoryService.reduceStock(product.getProductId(), orderItem.getQuantity());
            orderItem.setOrder(order);
            totalAmount += orderItem.getPriceAtPurchase() * orderItem.getQuantity();
        }

        if(order.getTotalAmount() == null){
            order.setTotalAmount(totalAmount);
        }
        //set order status to placed
        order.setOrderStatus(OrderStatus.PLACED);
        orderRepository.save(order);
        return"Order placed successfully";
    }


    @Override
    public String cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order Not Found"));
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
                .orElseThrow(() -> new IllegalArgumentException("Order Not Found"));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }
}
