package com.education.marketplace_3D.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreateDto (
        @NotBlank String description,
        @NotNull @Positive BigDecimal price,
        @Future LocalDateTime deadline
){

}
