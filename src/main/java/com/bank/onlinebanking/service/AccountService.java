package com.bank.onlinebanking.service;

import com.bank.onlinebanking.dto.BalanceResponse;
import com.bank.onlinebanking.dto.DashboardResponse;
import com.bank.onlinebanking.dto.DepositRequest;
import com.bank.onlinebanking.dto.DepositResponse;
import com.bank.onlinebanking.dto.ProfileResponse;

public interface AccountService {

    DepositResponse deposit(String email, DepositRequest request);

    BalanceResponse getBalance(String email);

    ProfileResponse getProfile(String email);

    DashboardResponse getDashboard(String email);
}