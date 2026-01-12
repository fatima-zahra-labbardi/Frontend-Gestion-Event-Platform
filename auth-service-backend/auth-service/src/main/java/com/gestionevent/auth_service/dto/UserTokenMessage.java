package com.gestionevent.auth_service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO pour notifier la mise à jour du token FCM via Kafka.
 * Correspond au contrat JSON requis par le service Consumer.
 */
public class UserTokenMessage {
    private Long userId;        // Integer/Long
    private String fcmToken;    // Obligatoire
    private String deviceType;  // "ANDROID", "IOS", ou "WEB"
    private String deviceInfo;  // Exemple: "Pixel 7"
    private String createdAt;   // Format ISO-8601
    private String updatedAt;   // Format ISO-8601

    // Constructeur par défaut (nécessaire pour la désérialisation JSON)
    public UserTokenMessage() {
    }

    // Constructeur complet pour faciliter l'envoi depuis le Controller
    public UserTokenMessage(Long userId, String fcmToken, String deviceType, String deviceInfo) {
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
        this.deviceInfo = deviceInfo;

        // Initialisation automatique des dates au format ISO-8601
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.createdAt = now;
        this.updatedAt = now;
    }

    // --- Getters et Setters ---

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}