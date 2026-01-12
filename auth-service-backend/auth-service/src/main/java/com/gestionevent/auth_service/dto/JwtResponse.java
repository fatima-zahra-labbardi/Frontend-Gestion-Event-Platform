package com.gestionevent.auth_service.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer"; // C'u est le standard pour les JWT
    private Long id;
    private String email;
    private String role;

    // Constructeur
    public JwtResponse(String accessToken, Long id, String email, String role) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // Getters et Setters (Indispensable pour que Spring puisse créer le JSON)
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}