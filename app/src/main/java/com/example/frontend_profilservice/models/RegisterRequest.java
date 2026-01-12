package com.example.frontend_profilservice.models;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String filiere;
    private String nomEtablissement;
    private String typeOrganisateur;

    public RegisterRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public String getNomEtablissement() { return nomEtablissement; }
    public void setNomEtablissement(String nomEtablissement) { this.nomEtablissement = nomEtablissement; }
    public String getTypeOrganisateur() { return typeOrganisateur; }
    public void setTypeOrganisateur(String typeOrganisateur) { this.typeOrganisateur = typeOrganisateur; }
}
