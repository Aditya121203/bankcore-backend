package com.bank.onlinebanking.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.onlinebanking.entity.Account;
import com.bank.onlinebanking.entity.Transaction;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    // ================= SENDER TRANSACTIONS =================

    List<Transaction> findBySenderAccount(Account account);

    // ================= PAGINATED TRANSACTION HISTORY =================

    List<Transaction> findBySenderAccountOrReceiverAccount(
            Account sender,
            Account receiver,
            Pageable pageable);

    // ================= ALL TRANSACTIONS =================

    List<Transaction> findBySenderAccountOrReceiverAccountOrderByTransactionDateDesc(
            Account sender,
            Account receiver);

    // ================= TRANSACTION COUNT =================

    long countBySenderAccountOrReceiverAccount(
            Account sender,
            Account receiver);

    // ================= LAST TRANSACTION =================

    Transaction findTopBySenderAccountOrReceiverAccountOrderByTransactionDateDesc(
            Account sender,
            Account receiver);

    // ================= TOTAL TRANSFERRED OUT TODAY (for daily limit) =================

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.senderAccount = :account " +
            "AND t.transactionType = com.bank.onlinebanking.entity.TransactionType.TRANSFER " +
            "AND t.transactionDate >= :startOfDay")
    BigDecimal sumTransferredOutSince(
            @Param("account") Account account,
            @Param("startOfDay") LocalDateTime startOfDay);

    // ================= TOTAL DEPOSITED TODAY (for daily deposit limit) =================

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.receiverAccount = :account " +
            "AND t.transactionType = com.bank.onlinebanking.entity.TransactionType.DEPOSIT " +
            "AND t.transactionDate >= :startOfDay")
    BigDecimal sumDepositedSince(
            @Param("account") Account account,
            @Param("startOfDay") LocalDateTime startOfDay);

    // ================= TOTAL WITHDRAWN TODAY (for daily withdrawal limit) =================

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.senderAccount = :account " +
            "AND t.transactionType = com.bank.onlinebanking.entity.TransactionType.WITHDRAW " +
            "AND t.transactionDate >= :startOfDay")
    BigDecimal sumWithdrawnSince(
            @Param("account") Account account,
            @Param("startOfDay") LocalDateTime startOfDay);
}