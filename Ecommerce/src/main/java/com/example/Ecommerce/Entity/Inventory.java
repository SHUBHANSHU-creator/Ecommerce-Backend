package com.example.Ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    //excluded from toString to prevent stackoverflow -> infinite recursion if tried to log or returned as json
    // else a DTO can be made and set data manually
    private Product product;
    private Integer quantity;
    private LocalDateTime creationDate;

    public Inventory(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    //used versioning for overselling
    //if two users try to grab a prod, one will succeed, second will fail since version increases
    //and his query fails i.e update .... quantity - 1 where version = x (x becomes x+1)
    //optimistic locking performed -> This approach is based on the assumption that a data conflict is rare.

    //pessimistic -> @Lock(LockModeType.PESSIMISTIC_WRITE)
    //              Optional<Product> findByIdLocked(Long id);
    //pessimistic locks it until the update transaction is over, which is wrong here.
    //if 1000 users tried to buy iphone at a time then they both have to wait until everyone else are done.
    @Version
    private Integer version;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

}
