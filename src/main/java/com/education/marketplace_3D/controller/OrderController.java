package com.education.marketplace_3D.controller;

import com.education.marketplace_3D.dto.OrderCreateDto;
import com.education.marketplace_3D.dto.OrderResponseDto;
import com.education.marketplace_3D.enums.OrderStatus;
import com.education.marketplace_3D.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Створення ногового замовлення")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UUID> createOrder (@Valid @RequestBody OrderCreateDto dto, Principal principal){
        UUID createdOrderId = orderService.createOrder(dto,principal.getName());
       return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderId);
    }

    @GetMapping("/my")
    @Operation(summary = "Отримання списку замовлень")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(Principal principal,
                                                              @RequestParam(required = false) OrderStatus status,
                                                              @RequestParam(required = false) BigDecimal minPrice,
                                                              @ParameterObject Pageable pageable){
        Page<OrderResponseDto> listOfDto = orderService.getMyOrders(principal.getName(), status, minPrice, pageable);
        return ResponseEntity.ok(listOfDto);
    }

    @PatchMapping("/{id}/take")
    @Operation(summary = "Взяття замовлення")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> takeOrder(@PathVariable UUID id, Principal principal){
        OrderResponseDto orderResponseDto = orderService.takeOrder(id, principal.getName());
        return ResponseEntity.ok(orderResponseDto);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Завершення замовлення")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> completeOrder(@PathVariable UUID id, Principal principal){
        OrderResponseDto orderResponseDto = orderService.completeOrder(id, principal.getName());
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping ("/feed")
    @Operation(summary = "Отримати доступні для прийняття замовлення")
    public ResponseEntity<Page<OrderResponseDto>> getAvailableOrders (@ParameterObject Pageable pageable){
        Page<OrderResponseDto> listOfOrders = orderService.getAvailableOrders(pageable);
        return ResponseEntity.ok(listOfOrders);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Відмінити створене замовлення")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> cancelOrder (@PathVariable UUID id, Principal principal){
        OrderResponseDto dto = orderService.cancelOrder(id, principal.getName());
        return ResponseEntity.ok(dto);
    }


}
