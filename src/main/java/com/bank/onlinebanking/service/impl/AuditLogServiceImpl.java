package com.bank.onlinebanking.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bank.onlinebanking.dto.AuditLogResponse;
import com.bank.onlinebanking.entity.AuditLog;
import com.bank.onlinebanking.entity.User;
import com.bank.onlinebanking.exception.ResourceNotFoundException;
import com.bank.onlinebanking.repository.AuditLogRepository;
import com.bank.onlinebanking.repository.UserRepository;
import com.bank.onlinebanking.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository,
                               UserRepository userRepository) {

        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveLog(String email,
                        String action,
                        String description) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        AuditLog log = new AuditLog();

        log.setUser(user);
        log.setAction(action);
        log.setDescription(description);
        log.setActionTime(LocalDateTime.now());

        auditLogRepository.save(log);
    }

    @Override
    public List<AuditLogResponse> getLogs(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return auditLogRepository.findByUserOrderByActionTimeDesc(user)
                .stream()
                .map(log -> {

                    AuditLogResponse response = new AuditLogResponse();

                    response.setAction(log.getAction());
                    response.setDescription(log.getDescription());
                    response.setActionTime(log.getActionTime());

                    return response;

                }).collect(Collectors.toList());
    }
}