package com.education.marketplace_3D.controller;

import com.education.marketplace_3D.dto.JwtAuthResponse;
import com.education.marketplace_3D.dto.UserLoginDto;
import com.education.marketplace_3D.dto.UserRegistrationDto;
import com.education.marketplace_3D.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

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
}
