package com.education.marketplace_3D.dto;

import com.education.marketplace_3D.enums.UserRole;

import java.math.BigDecimal;
import java.util.UUID;

public record UserProfileDto(
        UUID id,
        String email,
        String displayName,
        BigDecimal balance,
        UserRole role
) {
}
