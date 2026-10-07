package com.education.marketplace_3D.entities;

import com.education.marketplace_3D.enums.AuditAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "audit_log")
public class AuditLog{
    @Id
    @Setter (AccessLevel.NONE)
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated (EnumType.STRING)
    private AuditAction action;
    private String description;
    private String userEmail;
    @CurrentTimestamp
    private LocalDateTime createdAt;

    public AuditLog (AuditAction action, String description, String userEmail){
        this.action = action;
        this.description = description;
        this.userEmail = userEmail;
    }


}
