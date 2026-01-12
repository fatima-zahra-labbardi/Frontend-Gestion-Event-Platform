package com.gestionevent.auth_service.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // "STUDENT" ou "ORGANIZER"

    // --- Champs spécifiques (Mise à jour) ---

    @Column(name = "filiere")
    private String filiere; // Pour l'étudiant

    @Column(name = "nom_etablissement")
    private String nomEtablissement; // École (Étudiant) OU Organisation (Organisateur)

    @Column(name = "type_organisateur")
    private String typeOrganisateur; // Pour l'organisateur (Club, Asso, etc.)

    // --- Constructeurs ---
    public User() {
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getNomEtablissement() {
        return nomEtablissement;
    }

    public void setNomEtablissement(String nomEtablissement) {
        this.nomEtablissement = nomEtablissement;
    }

    public String getTypeOrganisateur() {
        return typeOrganisateur;
    }

    public void setTypeOrganisateur(String typeOrganisateur) {
        this.typeOrganisateur = typeOrganisateur;
    }
}