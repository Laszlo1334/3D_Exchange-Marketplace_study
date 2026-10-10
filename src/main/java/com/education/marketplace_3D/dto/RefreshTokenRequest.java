package com.education.marketplace_3D.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token cannot be blank")
        String refreshToken
){
}
