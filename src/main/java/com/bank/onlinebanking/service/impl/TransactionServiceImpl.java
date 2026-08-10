package com.bank.onlinebanking.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.onlinebanking.dto.TransactionResponse;
import com.bank.onlinebanking.dto.TransferRequest;
import com.bank.onlinebanking.dto.WithdrawRequest;
import com.bank.onlinebanking.entity.Account;
import com.bank.onlinebanking.entity.Transaction;
import com.bank.onlinebanking.entity.TransactionType;
import com.bank.onlinebanking.entity.User;
import com.bank.onlinebanking.exception.InsufficientBalanceException;
import com.bank.onlinebanking.exception.ResourceNotFoundException;
import com.bank.onlinebanking.repository.AccountRepository;
import com.bank.onlinebanking.repository.TransactionRepository;
import com.bank.onlinebanking.repository.UserRepository;
import com.bank.onlinebanking.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // =========================================================
    // WITHDRAW
    // =========================================================

    @Override
    @Transactional
    public TransactionResponse withdraw(
            String email,
            WithdrawRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));

        BigDecimal amount = request.getAmount();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance.");
        }

        account.setBalance(
                account.getBalance().subtract(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setSenderAccount(account);
        transaction.setReceiverAccount(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(
                TransactionType.WITHDRAW);

        if (request.getDescription() != null
                && !request.getDescription().trim().isEmpty()) {

            transaction.setDescription(
                    request.getDescription());

        } else {

            transaction.setDescription(
                    "Money Withdrawn");
        }

        transaction.setTransactionDate(
                LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }

    // =========================================================
    // TRANSFER
    // =========================================================

    @Override
    @Transactional
    public TransactionResponse transfer(
            String email,
            TransferRequest request) {

        User senderUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sender user not found."));

        Account senderAccount =
                accountRepository.findByUser(senderUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sender account not found."));

        Account receiverAccount =
                accountRepository
                        .findByAccountNumber(
                                request.getReceiverAccountNumber())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Receiver account not found."));

        BigDecimal amount = request.getAmount();

        if (senderAccount.getAccountNumber()
                .equals(receiverAccount.getAccountNumber())) {

            throw new IllegalArgumentException(
                    "Cannot transfer money to the same account.");
        }

        if (senderAccount.getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance.");
        }

        // Deduct money from sender
        senderAccount.setBalance(
                senderAccount.getBalance()
                        .subtract(amount));

        // Add money to receiver
        receiverAccount.setBalance(
                receiverAccount.getBalance()
                        .add(amount));

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        // Create transaction
        Transaction transaction = new Transaction();

        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(
                TransactionType.TRANSFER);

        if (request.getDescription() != null
                && !request.getDescription().trim().isEmpty()) {

            transaction.setDescription(
                    request.getDescription());

        } else {

            transaction.setDescription(
                    "Money Transferred");
        }

        transaction.setTransactionDate(
                LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }

    // =========================================================
    // MINI STATEMENT
    // =========================================================

    @Override
    public List<TransactionResponse> getMiniStatement(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));

        Account account =
                accountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found."));

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderAccountOrReceiverAccountOrderByTransactionDateDesc(
                                account,
                                account);

        return transactions.stream()
                .limit(5)
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // ACCOUNT STATEMENT
    // =========================================================

    @Override
    public List<TransactionResponse> getAccountStatement(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));

        Account account =
                accountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found."));

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderAccountOrReceiverAccountOrderByTransactionDateDesc(
                                account,
                                account);

        return transactions.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // PAGINATED TRANSACTION HISTORY
    // =========================================================

    @Override
    public List<TransactionResponse> getTransactionHistory(
            String email,
            int page,
            int size) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));

        Account account =
                accountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found."));

        Pageable pageable =
                PageRequest.of(page, size);

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderAccountOrReceiverAccount(
                                account,
                                account,
                                pageable);

        return transactions.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // CONVERT ENTITY TO RESPONSE
    // =========================================================

    private TransactionResponse convertToResponse(
            Transaction transaction) {

        TransactionResponse response =
                new TransactionResponse();

        response.setTransactionId(
                transaction.getId());

        response.setSenderAccount(
                transaction.getSenderAccount() != null
                        ? transaction.getSenderAccount()
                                .getAccountNumber()
                        : null);

        response.setReceiverAccount(
                transaction.getReceiverAccount() != null
                        ? transaction.getReceiverAccount()
                                .getAccountNumber()
                        : null);

        response.setAmount(
                transaction.getAmount());

        response.setTransactionType(
                transaction.getTransactionType().name());

        response.setDescription(
                transaction.getDescription());

        response.setTransactionDate(
                transaction.getTransactionDate());

        return response;
    }
}