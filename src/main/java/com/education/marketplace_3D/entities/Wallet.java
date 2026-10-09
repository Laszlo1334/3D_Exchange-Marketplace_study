package com.education.marketplace_3D.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor

public class Wallet extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private BigDecimal balance;
    @Version
    private Integer version;
    private BigDecimal holdBalance;

    public Wallet (User user){
        this.user = user;
        this.balance = BigDecimal.ZERO;
        this.holdBalance = BigDecimal.ZERO;
    }
}
