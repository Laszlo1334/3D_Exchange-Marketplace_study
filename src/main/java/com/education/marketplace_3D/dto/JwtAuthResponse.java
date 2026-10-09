package com.education.marketplace_3D.dto;

public record JwtAuthResponse (
        String accessToken,
        String refreshToken
){}

