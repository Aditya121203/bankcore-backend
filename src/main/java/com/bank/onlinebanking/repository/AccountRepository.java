package com.bank.onlinebanking.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.onlinebanking.entity.Account;
import com.bank.onlinebanking.entity.User;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByUser(User user);

}