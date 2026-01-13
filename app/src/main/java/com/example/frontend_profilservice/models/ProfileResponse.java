package com.example.frontend_profilservice.models;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    private Long id;
    private Long userId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("userType")
    private String userType;
    @SerializedName("institution")
    private String institution;
    @SerializedName("major")
    private String major;
    @SerializedName("organizationName")
    private String organizationName;
    @SerializedName("organizationType")
    private String organizationType;
    @SerializedName("bio")
    private String bio;
    private String profilePicUrl;
    private String lastLogin;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getUserType() { return userType; }
    public String getInstitution() { return institution; }
    public String getMajor() { return major; }
    public String getOrganizationName() { return organizationName; }
    public String getOrganizationType() { return organizationType; }
    public String getBio() { return bio; }
    public String getProfilePicUrl() { return profilePicUrl; }
    public String getLastLogin() { return lastLogin; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setInstitution(String institution) { this.institution = institution; }
    public void setMajor(String major) { this.major = major; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
    public void setUserType(String userType) { this.userType = userType; }
}
