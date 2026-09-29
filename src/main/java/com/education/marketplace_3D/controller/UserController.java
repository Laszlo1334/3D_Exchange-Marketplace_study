package com.education.marketplace_3D.controller;

import com.education.marketplace_3D.dto.UserProfileDto;
import com.education.marketplace_3D.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Отримання профілю поточного користувача")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> getMyProfile (Principal principal){
        UserProfileDto userProfile = userService.getUserProfile(principal.getName());
        return ResponseEntity.ok(userProfile);

    }

}
