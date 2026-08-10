package com.bank.onlinebanking.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}