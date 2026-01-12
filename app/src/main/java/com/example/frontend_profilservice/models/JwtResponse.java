package com.example.frontend_profilservice.models;

public class JwtResponse {
    private String token;
    private Long id;
    private String email;
    private String role;

    public JwtResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
