package com.education.marketplace_3D.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TopUpRequestDto(
        @NotNull @DecimalMin(value = "1.00") BigDecimal amount
        ) {
}
