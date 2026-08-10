package com.bank.onlinebanking.dto;

import java.math.BigDecimal;

public class DepositResponse {

    private String message;
    private String accountNumber;
    private BigDecimal depositedAmount;
    private BigDecimal currentBalance;

    public DepositResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getDepositedAmount() {
        return depositedAmount;
    }

    public void setDepositedAmount(BigDecimal depositedAmount) {
        this.depositedAmount = depositedAmount;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
}