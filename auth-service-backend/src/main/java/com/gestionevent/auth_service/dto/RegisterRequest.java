package com.gestionevent.auth_service.dto;

public class RegisterRequest {

    // --- Champs Communs ---
    private String fullName;
    private String email;
    private String password;
    private String role; // "STUDENT" ou "ORGANIZER" (Essentiel pour la DB)

    // --- Champs Spécifiques (Simplifiés) ---
    // Pour l'étudiant : Filière
    private String filiere;

    // Pour l'étudiant : Établissement
    // POUR l'organisateur : Nom de l'organisation
    // (On réutilise le champ existant dans la DB)
    private String nomEtablissement;

    // Pour l'organisateur : Type (Club, Asso, etc.)
    private String typeOrganisateur;

    // --- Constructeurs ---
    public RegisterRequest() {
    }

    // --- Getters et Setters ---

    // Communs
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Filière (Étudiant uniquement)
    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    // Ce champ porte deux casquettes maintenant :
    // 1. Établissement de l'étudiant
    // 2. Nom de l'organisation de l'organisateur
    public String getNomEtablissement() {
        return nomEtablissement;
    }

    public void setNomEtablissement(String nomEtablissement) {
        this.nomEtablissement = nomEtablissement;
    }

    // Type Organisateur (Organisateur uniquement)
    public String getTypeOrganisateur() {
        return typeOrganisateur;
    }

    public void setTypeOrganisateur(String typeOrganisateur) {
        this.typeOrganisateur = typeOrganisateur;
    }
}