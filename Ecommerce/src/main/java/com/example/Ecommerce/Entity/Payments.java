package com.example.Ecommerce.Entity;

import com.example.Ecommerce.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double totalAmount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
}
