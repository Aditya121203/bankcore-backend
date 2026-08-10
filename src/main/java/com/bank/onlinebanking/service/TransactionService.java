package com.bank.onlinebanking.service;

import java.util.List;

import com.bank.onlinebanking.dto.TransactionResponse;
import com.bank.onlinebanking.dto.TransferRequest;
import com.bank.onlinebanking.dto.WithdrawRequest;

public interface TransactionService {

    TransactionResponse transfer(
            String email,
            TransferRequest request);

    TransactionResponse withdraw(
            String email,
            WithdrawRequest request);

    List<TransactionResponse> getTransactionHistory(
            String email,
            int page,
            int size);

    List<TransactionResponse> getAccountStatement(
            String email);

    List<TransactionResponse> getMiniStatement(
            String email);
}