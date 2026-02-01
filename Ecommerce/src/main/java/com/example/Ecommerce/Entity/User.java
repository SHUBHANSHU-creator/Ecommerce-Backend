package com.example.Ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @ToString.Exclude
    private String password;
    private String phoneNumber;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

}
