package com.bank.onlinebanking.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private String action;
    private String description;
    private LocalDateTime actionTime;

    public AuditLogResponse() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }
}