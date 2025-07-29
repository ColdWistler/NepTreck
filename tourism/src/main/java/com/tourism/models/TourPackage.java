package com.tourism.models;

import java.time.LocalDateTime;
import java.util.List;

public class TourPackage {
    private String packageId;
    private String packageName;
    private String description;
    private Double price;
    private Integer duration; // in days
    private String category;
    private String destination;
    private String inclusions;
    private String exclusions;
    private Integer maxParticipants;
    private boolean active;
    private LocalDateTime createdAt;
    private String difficulty; // EASY, MODERATE, HARD
    private String season; // SPRING, SUMMER, AUTUMN, WINTER, ALL_YEAR

    // Constructors
    public TourPackage() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public TourPackage(String packageId, String packageName, String description, Double price, Integer duration, String category) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.category = category;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getInclusions() {
        return inclusions;
    }

    public void setInclusions(String inclusions) {
        this.inclusions = inclusions;
    }

    public String getExclusions() {
        return exclusions;
    }

    public void setExclusions(String exclusions) {
        this.exclusions = exclusions;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    @Override
    public String toString() {
        return packageName != null ? packageName : packageId;
    }

    public String getDetailedInfo() {
        return "TourPackage{" +
                "packageId='" + packageId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", category='" + category + '\'' +
                ", active=" + active +
                '}';
    }
}