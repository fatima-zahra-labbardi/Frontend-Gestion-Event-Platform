package com.gestionevent.auth_service.dto;

/**
 * Data Transfer Object pour la connexion.
 * Contient les identifiants et les informations du mobile pour Kafka.
 */
public class LoginRequest {

    private String email;
    private String password;

    // --- Nouveaux champs pour le service de ton amie ---
    private String fcmToken;    // Obligatoire pour les notifications
    private String deviceType;  // "ANDROID", "IOS", ou "WEB"
    private String deviceInfo;  // Exemple: "Pixel 7" ou "Samsung S21"

    // --- Constructeur par défaut (nécessaire pour la désérialisation Jackson) ---
    public LoginRequest() {
    }

    // --- Getters et Setters ---

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}