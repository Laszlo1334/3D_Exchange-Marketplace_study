package com.education.marketplace_3D.dto;

import com.education.marketplace_3D.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        String description,
        BigDecimal price,
        UserSummaryDto customer,
        UserSummaryDto executor,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime deadline
) {
}
