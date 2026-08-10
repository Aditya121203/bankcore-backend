package com.bank.onlinebanking.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.onlinebanking.dto.AuditLogResponse;
import com.bank.onlinebanking.service.AuditLogService;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<AuditLogResponse>> getAuditHistory(
            Principal principal) {

        return ResponseEntity.ok(
                auditLogService.getLogs(principal.getName()));
    }
}