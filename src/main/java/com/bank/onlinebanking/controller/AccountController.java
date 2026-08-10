package com.bank.onlinebanking.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bank.onlinebanking.dto.DashboardResponse;

import com.bank.onlinebanking.dto.BalanceResponse;
import com.bank.onlinebanking.dto.DepositRequest;
import com.bank.onlinebanking.dto.DepositResponse;
import com.bank.onlinebanking.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ================= Deposit =================

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(
            Principal principal,
            @Valid @RequestBody DepositRequest request) {

        DepositResponse response =
                accountService.deposit(principal.getName(), request);

        return ResponseEntity.ok(response);
    }

    // ================= Balance =================

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            Principal principal) {

        BalanceResponse response =
                accountService.getBalance(principal.getName());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            Principal principal) {

        DashboardResponse response =
                accountService.getDashboard(principal.getName());

        return ResponseEntity.ok(response);
    }
}