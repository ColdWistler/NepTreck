package com.tourism.models;

import java.time.LocalDateTime;

public class Tourist {
    private String touristId;
    private String accountId; // Corresponds to parts[1]
    private String fullName;  // Corresponds to parts[2]
    private String email;     // Corresponds to parts[3]
    private String phoneNumber; // Corresponds to parts[4]
    private String nationality; // Corresponds to parts[5]
    private boolean active;     // Corresponds to parts[6]
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Tourist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true; // Default to active
    }

    // Constructor matching the arguments from TouristController's handleCreateTourist
    // (touristId, accountId, fullName, email, phoneNumber, nationality)
    // Note: The 'accountId' can be null initially, or generated.
    public Tourist(String touristId, String accountId, String fullName, String email, String phoneNumber, String nationality) {
        this(); // Call default constructor for createdAt, updatedAt, active
        this.touristId = touristId;
        this.accountId = accountId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationality = nationality;
    }

    // Constructor used by parseTouristFromString in FileDataManager (fewer initial arguments)
    // You might also use this or the default constructor in parseTouristFromString
    // if you assign properties one by one.

    // Getters and Setters
    public String getTouristId() { return touristId; }
    public void setTouristId(String touristId) { this.touristId = touristId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Tourist{" +
               "touristId='" + touristId + '\'' +
               ", fullName='" + fullName + '\'' +
               ", email='" + email + '\'' +
               ", active=" + active +
               '}';
    }
}
