package com.example.frontend_profilservice.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class EventResponse implements Serializable {
    private Long id;
    private String title;
    private String description;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("eventDate")
    private String eventDate;
    
    private String location;
    
    @SerializedName("maxParticipants")
    private Integer maxParticipants;
    
    @SerializedName("createdBy")
    private Long createdBy;
    
    private String status;

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public Long getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventResponse that = (EventResponse) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
