package com.gestionevent.auth_service.controllers;

import com.gestionevent.auth_service.dto.JwtResponse;
import com.gestionevent.auth_service.dto.LoginRequest;
import com.gestionevent.auth_service.dto.RegisterRequest;
import com.gestionevent.auth_service.dto.UserAuthenticatedMessage;
import com.gestionevent.auth_service.dto.UserTokenMessage;
import com.gestionevent.auth_service.entities.User;
import com.gestionevent.auth_service.repositories.UserRepository;
import com.gestionevent.auth_service.services.JwtUtils;
import com.gestionevent.auth_service.services.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private JwtUtils jwtUtils;

    // --- Inscription Étudiant ---
    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterRequest request) {
        return saveUser(request, "STUDENT");
    }

    // --- Inscription Organisateur ---
    @PostMapping("/register/organizer")
    public ResponseEntity<?> registerOrganizer(@RequestBody RegisterRequest request) {
        return saveUser(request, "ORGANIZER");
    }

    // --- Connexion ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                        // Kafka - Notification / Tokens
                        UserTokenMessage tokenMessage = new UserTokenMessage(
                                user.getId(),
                                request.getFcmToken() != null ? request.getFcmToken() : "token_fcm_test",
                                request.getDeviceType() != null ? request.getDeviceType() : "ANDROID",
                                request.getDeviceInfo() != null ? request.getDeviceInfo() : "Mobile Device"
                        );
                        kafkaProducerService.sendTokenUpdate(tokenMessage);

                        // Kafka - Service Profil
                        UserAuthenticatedMessage profileMessage = new UserAuthenticatedMessage(
                                user.getId(),
                                user.getEmail(),
                                user.getFullName(),
                                user.getRole()
                        );
                        kafkaProducerService.sendUserAuthenticated(profileMessage);

                        // Token JWT
                        String jwt = jwtUtils.generateToken(user.getEmail(), user.getRole(), user.getId());

                        return ResponseEntity.ok(new JwtResponse(
                                jwt,
                                user.getId(),
                                user.getEmail(),
                                user.getRole()
                        ));

                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé"));
    }

    // Méthode utilitaire pour traiter l'inscription (Mise à jour)
    private ResponseEntity<?> saveUser(RegisterRequest request, String role) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Erreur : Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        // On utilise la même colonne pour l'école (Étudiant) ou l'organisation (Organisateur)
        user.setNomEtablissement(request.getNomEtablissement());

        if ("STUDENT".equals(role)) {
            // CNE et Niveau supprimés comme convenu
            user.setFiliere(request.getFiliere());
        } else if ("ORGANIZER".equals(role)) {
            user.setTypeOrganisateur(request.getTypeOrganisateur());
        }

        userRepository.save(user);

        // Notification Kafka
        String kafkaMsg = "NOUVEL_UTILISATEUR|" + role + "|" + user.getEmail() + "|" + user.getFullName();
        try {
            kafkaProducerService.sendMessage(kafkaMsg);
        } catch (Exception e) {
            System.err.println("Erreur notification Kafka inscription : " + e.getMessage());
        }

        String message = ("STUDENT".equals(role) ? "Étudiant" : "Organisateur") + " inscrit avec succès !";
        return ResponseEntity.ok(message);
    }
}