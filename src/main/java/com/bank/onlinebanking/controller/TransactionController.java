package com.bank.onlinebanking.controller;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bank.onlinebanking.dto.TransactionResponse;
import com.bank.onlinebanking.dto.TransferRequest;
import com.bank.onlinebanking.dto.WithdrawRequest;
import com.bank.onlinebanking.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ================= Transfer =================

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        TransactionResponse response =
                transactionService.transfer(authentication.getName(), request);

        return ResponseEntity.ok(response);
    }

    // ================= Withdraw =================

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {

        TransactionResponse response =
                transactionService.withdraw(authentication.getName(), request);

        return ResponseEntity.ok(response);
    }

    // ================= Transaction History =================

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> history(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                transactionService.getTransactionHistory(
                        authentication.getName(),
                        page,
                        size));
    }

    // ================= Account Statement =================

    @GetMapping("/statement")
    public ResponseEntity<List<TransactionResponse>> statement(
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getAccountStatement(authentication.getName()));
    }

    // ================= Mini Statement =================

    @GetMapping("/mini-statement")
    public ResponseEntity<List<TransactionResponse>> miniStatement(
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getMiniStatement(authentication.getName()));
    }
}