package com.bank.onlinebanking.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.onlinebanking.dto.BalanceResponse;
import com.bank.onlinebanking.dto.DashboardResponse;
import com.bank.onlinebanking.dto.DepositRequest;
import com.bank.onlinebanking.dto.DepositResponse;
import com.bank.onlinebanking.dto.ProfileResponse;
import com.bank.onlinebanking.entity.Account;
import com.bank.onlinebanking.entity.Transaction;
import com.bank.onlinebanking.entity.TransactionType;
import com.bank.onlinebanking.entity.User;
import com.bank.onlinebanking.exception.ResourceNotFoundException;
import com.bank.onlinebanking.repository.AccountRepository;
import com.bank.onlinebanking.repository.TransactionRepository;
import com.bank.onlinebanking.repository.UserRepository;
import com.bank.onlinebanking.service.AccountService;
import com.bank.onlinebanking.service.AuditLogService;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;

    public AccountServiceImpl(UserRepository userRepository,
                              AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              AuditLogService auditLogService) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
    }

    // ===================== DEPOSIT =====================

    @Override
    @Transactional
    public DepositResponse deposit(String email, DepositRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));

        BigDecimal newBalance = account.getBalance().add(request.getAmount());

        account.setBalance(newBalance);

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setSenderAccount(null);
        transaction.setReceiverAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(TransactionType.DEPOSIT);

        if (request.getDescription() != null &&
                !request.getDescription().trim().isEmpty()) {

            transaction.setDescription(request.getDescription());

        } else {

            transaction.setDescription("Money Deposited");
        }

        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);

        // ================= AUDIT LOG =================

        auditLogService.saveLog(
                email,
                "DEPOSIT",
                "Deposited ₹" + request.getAmount());

        DepositResponse response = new DepositResponse();

        response.setMessage("Amount Deposited Successfully");
        response.setAccountNumber(account.getAccountNumber());
        response.setDepositedAmount(request.getAmount());
        response.setCurrentBalance(account.getBalance());

        return response;
    }

    // ===================== BALANCE =====================

    @Override
    public BalanceResponse getBalance(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));

        BalanceResponse response = new BalanceResponse();

        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus().name());

        return response;
    }

    // ===================== PROFILE =====================

    @Override
    public ProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));

        ProfileResponse response = new ProfileResponse();

        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus().name());

        return response;
    }

    // ===================== DASHBOARD =====================

    @Override
    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));

        DashboardResponse response = new DashboardResponse();

        response.setFullName(user.getFullName());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus().name());

        long totalTransactions =
                transactionRepository.countBySenderAccountOrReceiverAccount(
                        account,
                        account);

        response.setTotalTransactions(totalTransactions);

        Transaction lastTransaction =
                transactionRepository
                        .findTopBySenderAccountOrReceiverAccountOrderByTransactionDateDesc(
                                account,
                                account);

        if (lastTransaction != null) {

            response.setLastTransactionType(
                    lastTransaction.getTransactionType().name());

            response.setLastTransactionAmount(
                    lastTransaction.getAmount());

            response.setLastTransactionDate(
                    lastTransaction.getTransactionDate());
        }

        return response;
    }
}