package com.education.marketplace_3D.entities;

import com.education.marketplace_3D.enums.PaymentMethod;
import com.education.marketplace_3D.enums.TransactionStatus;
import com.education.marketplace_3D.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table (name = "transactions")
@Getter
@Setter
public class Transaction extends BaseAuditingEntity{
    @Id
    @Setter (AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "order_id")
    private Order order;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "sender_id")
    private User sender;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "receiver_id")
    private User receiver;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    public Transaction (
            Order order,
            User sender,
            User receiver,
            BigDecimal amount,
            TransactionStatus status,
            PaymentMethod paymentMethod,
            TransactionType type){
        this.order = order;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.type = type;
    }

    public Transaction (
            User receiver,
            BigDecimal amount,
            TransactionStatus status,
            PaymentMethod paymentMethod,
            TransactionType type
    ){
        this.receiver = receiver;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.type = type;
    }

}
