package com.education.marketplace_3D.controller;

import com.education.marketplace_3D.dto.TopUpRequestDto;
import com.education.marketplace_3D.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping ("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/top-up")
    @Operation (summary = "Депозит на гаманець")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> topUp (@RequestBody @Valid TopUpRequestDto dto, Principal principal){
        walletService.topUpBalance(principal.getName(), dto.amount());
        return ResponseEntity.ok().build();
    }
}
