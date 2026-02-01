package com.example.Ecommerce.Entity;

import com.example.Ecommerce.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // order is often a keyword in SQL so renaming
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private String orderName;
    private LocalDateTime orderDate;
    private Double totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;
    private String shippingAddress;

    //options: Lazy or Eager. Use lazy when the user is not immediately required else use Eager
    //default will also be eager only here for many to one relationship since only one record needs to be fetched
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne
    private Payments payments;


    // cascade = ALL: If you save the Order, save all the items automatically.
    // orphanRemoval = true: If you remove an item from the list, delete it from the DB.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();


    public Order(String OrderName,Double totalAmount,User user, String shippingAddress) {
        this.orderName = OrderName;
        this.orderDate = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.user = user;
    }

}
