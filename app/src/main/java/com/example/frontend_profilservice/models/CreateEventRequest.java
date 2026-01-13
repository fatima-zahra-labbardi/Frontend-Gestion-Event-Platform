package com.example.frontend_profilservice.models;

import java.io.Serializable;

public class CreateEventRequest implements Serializable {
    private String title;
    private String description;
    private String eventDate; // ISO format or as required by backend
    private String location;
    private Integer maxParticipants;

    public CreateEventRequest(String title, String description, String eventDate, String location, Integer maxParticipants) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.maxParticipants = maxParticipants;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
}
