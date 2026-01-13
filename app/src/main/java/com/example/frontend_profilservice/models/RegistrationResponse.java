package com.example.frontend_profilservice.models;

import java.io.Serializable;

public class RegistrationResponse implements Serializable {
    private Long id;
    private Long userId;
    private Long eventId;
    private String registeredAt;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getEventId() { return eventId; }
    public String getRegisteredAt() { return registeredAt; }
}
