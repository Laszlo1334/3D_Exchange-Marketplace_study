package com.education.marketplace_3D.entities;

import com.education.marketplace_3D.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name ="orders")
public class Order extends BaseAuditingEntity{
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Setter (AccessLevel.NONE)
    private UUID id;
    private String description;
    private BigDecimal price;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "customer_id", nullable = false)
    private User customer;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "executor_id", nullable = true)
    private User executor;
    @Enumerated (EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime deadline;
    private LocalDateTime finishedAt;
    @Version
    private Integer version;
    @Column(length = 500)
    private String resultFileUrl;

    public Order (String description, BigDecimal price, User customer,OrderStatus status, LocalDateTime deadline){
        this.description = description;
        this.price = price;
        this.customer = customer;
        this.status = status;
        this.deadline = deadline;
    }

    public Order (String description, BigDecimal price, User customer,OrderStatus status){
        this.description = description;
        this.price = price;
        this.customer = customer;
        this.status = status;
    }
}
