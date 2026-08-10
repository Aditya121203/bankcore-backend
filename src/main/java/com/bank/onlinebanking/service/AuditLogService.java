package com.bank.onlinebanking.service;

import java.util.List;

import com.bank.onlinebanking.dto.AuditLogResponse;

public interface AuditLogService {

    void saveLog(String email,
                 String action,
                 String description);

    List<AuditLogResponse> getLogs(String email);

}