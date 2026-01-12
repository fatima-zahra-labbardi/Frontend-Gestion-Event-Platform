package com.example.frontend_profilservice.models;

public class LoginRequest {
    private String email;
    private String password;
    private String fcmToken;
    private String deviceType;
    private String deviceInfo;

    public LoginRequest() {}

    public LoginRequest(String email, String password, String fcmToken, String deviceType, String deviceInfo) {
        this.email = email;
        this.password = password;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
        this.deviceInfo = deviceInfo;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
}
