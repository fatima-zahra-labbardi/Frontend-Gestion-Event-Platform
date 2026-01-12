package com.gestionevent.auth_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionevent.auth_service.dto.UserAuthenticatedMessage; // Nouveau DTO pour Salma
import com.gestionevent.auth_service.dto.UserTokenMessage; // DTO pour ton autre amie
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Initialisation manuelle pour éviter l'erreur de Bean "ObjectMapper not found"
    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- Topics Kafka ---
    private static final String TOPIC_REGISTRATION = "user-registration";
    private static final String TOPIC_TOKENS = "user.tokens.updated"; //
    private static final String TOPIC_AUTH_PROFIL = "user.authenticated"; //

    /**
     * Envoie un message texte simple pour les inscriptions (ton premier test).
     */
    public void sendMessage(String message) {
        System.out.println(">>> Kafka (Registration) : " + message);
        this.kafkaTemplate.send(TOPIC_REGISTRATION, message);
    }

    /**
     * Envoie le token FCM au service de notification (Amie 1).
     */
    public void sendTokenUpdate(UserTokenMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            System.out.println(">>> Kafka (Token Update) vers " + TOPIC_TOKENS + " : " + jsonMessage);
            this.kafkaTemplate.send(TOPIC_TOKENS, jsonMessage);
        } catch (Exception e) {
            System.err.println("ERREUR JSON TOKEN : " + e.getMessage());
        }
    }

    /**
     * Envoie les infos d'identité au Service de Profil (Amie 2 / Salma).
     */
    public void sendUserAuthenticated(UserAuthenticatedMessage message) {
        try {
            // Transformation de l'objet en JSON pour respecter le contrat
            String jsonMessage = objectMapper.writeValueAsString(message);
            System.out.println(">>> Kafka (Profile Sync) vers " + TOPIC_AUTH_PROFIL + " : " + jsonMessage);

            // Envoi sur le topic spécifique attendu par le Service Profil
            this.kafkaTemplate.send(TOPIC_AUTH_PROFIL, jsonMessage);
        } catch (Exception e) {
            System.err.println("ERREUR JSON PROFIL : " + e.getMessage());
        }
    }
}