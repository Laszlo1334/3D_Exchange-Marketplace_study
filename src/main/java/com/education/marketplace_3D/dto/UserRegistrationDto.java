package com.education.marketplace_3D.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationDto(
        @NotBlank String displayName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Пароль має містити мінімум 8 символів") String password)
{

}
