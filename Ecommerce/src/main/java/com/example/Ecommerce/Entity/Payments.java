package com.example.Ecommerce.Entity;

import com.example.Ecommerce.Enums.PaymentMethod;
import com.example.Ecommerce.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double totalAmount;
    @OneToOne
    private Order order;
    private Double paymentAmount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String transactionId; // The ID from the external provider
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private LocalDateTime paymentDate;

    @PrePersist
    protected void paymentDate() {
        this.paymentDate = LocalDateTime.now();
    }
}
