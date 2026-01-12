package com.gestionevent.auth_service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserAuthenticatedMessage {
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String loginTime;

    public UserAuthenticatedMessage() {}

    public UserAuthenticatedMessage(Long userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        // Format ISO-8601 requis
        this.loginTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // Getters et Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }
}