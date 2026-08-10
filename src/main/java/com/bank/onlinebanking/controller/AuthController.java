package com.bank.onlinebanking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.onlinebanking.dto.LoginRequest;
import com.bank.onlinebanking.dto.LoginResponse;
import com.bank.onlinebanking.dto.RegisterRequest;
import com.bank.onlinebanking.dto.UserResponse;
import com.bank.onlinebanking.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ================= Register =================

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(userService.register(request));
    }

    // ================= Login =================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(userService.login(request));
    }
}