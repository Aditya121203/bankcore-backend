package com.bank.onlinebanking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.onlinebanking.entity.AuditLog;
import com.bank.onlinebanking.entity.User;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByActionTimeDesc(User user);

}