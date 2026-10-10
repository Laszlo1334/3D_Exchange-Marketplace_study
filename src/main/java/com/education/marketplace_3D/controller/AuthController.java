package com.education.marketplace_3D.controller;

import com.education.marketplace_3D.dto.*;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.service.RefreshTokenService;
import com.education.marketplace_3D.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationDto user){
        userService.registerUser(user);
        return ResponseEntity.ok("Користувач успішно зареєстрований");

    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> loginUser(@Valid @RequestBody UserLoginDto user){
        JwtAuthResponse sessionToken = userService.loginUser(user);
        return ResponseEntity.ok(sessionToken);
    }

    @PostMapping ("/logout")
    public ResponseEntity<Void> logoutUser (@Valid @RequestBody RefreshTokenRequest request){
        refreshTokenService.logoutUser(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping ("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshAccessToken (@Valid @RequestBody RefreshTokenRequest request){
        JwtAuthResponse response = refreshTokenService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }


}
