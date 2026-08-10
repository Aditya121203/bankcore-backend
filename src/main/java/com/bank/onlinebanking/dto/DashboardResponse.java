package com.bank.onlinebanking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DashboardResponse {

    private String fullName;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;

    private long totalTransactions;

    private String lastTransactionType;
    private BigDecimal lastTransactionAmount;
    private LocalDateTime lastTransactionDate;

    public DashboardResponse() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public String getLastTransactionType() {
        return lastTransactionType;
    }

    public void setLastTransactionType(String lastTransactionType) {
        this.lastTransactionType = lastTransactionType;
    }

    public BigDecimal getLastTransactionAmount() {
        return lastTransactionAmount;
    }

    public void setLastTransactionAmount(BigDecimal lastTransactionAmount) {
        this.lastTransactionAmount = lastTransactionAmount;
    }

    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }
}