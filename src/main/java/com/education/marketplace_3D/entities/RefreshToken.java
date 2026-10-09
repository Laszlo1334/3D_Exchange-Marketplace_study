package com.education.marketplace_3D.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table (name = "refresh_token")
public class RefreshToken {
    @Id
    @Setter (AccessLevel.NONE)
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;
    private String token;
    private LocalDateTime expiredAt;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private User user;

    public RefreshToken (String token, LocalDateTime expiredAt, User user){
        this.token = token;
        this.expiredAt = expiredAt;
        this.user = user;
    }
}
