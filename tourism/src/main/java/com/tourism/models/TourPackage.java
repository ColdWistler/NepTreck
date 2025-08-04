package com.tourism.models;

public class TourPackage {
    private String packageId;
    private String name;
    private String description;
    private Double price;
    private Integer durationDays; // duration in days
    private String type;
    private String destination;
    private String difficulty;
    private String season;
    private Integer maxParticipants;
    private Double maxAltitude;
    private String category;
    private boolean active = true;

    // Basic constructor
    public TourPackage(String packageId, String name, String description, Double price, Integer durationDays, String type) {
        this.packageId = packageId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.type = type;
        this.destination = "N/A";
        this.difficulty = "N/A";
        this.season = "N/A";
        this.maxParticipants = 0;
        this.maxAltitude = 0.0;
        this.category = "Uncategorized";
    }
    public TourPackage() {
        this.active = true;
    }

    // Full constructor
    public TourPackage(String packageId, String name, String description, Double price,
                       Integer durationDays, String type, String destination, String difficulty,
                       String season, Integer maxParticipants, Double maxAltitude, String category) {
        this.packageId = packageId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.type = type;
        this.destination = destination;
        this.difficulty = difficulty;
        this.season = season;
        this.maxParticipants = maxParticipants;
        this.maxAltitude = maxAltitude;
        this.category = category;
    }

    // Getters and Setters
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPackageName() { return getName(); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Double getMaxAltitude() { return maxAltitude; }
    public void setMaxAltitude(Double maxAltitude) { this.maxAltitude = maxAltitude; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "TourPackage{" +
                "packageId='" + packageId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", durationDays=" + durationDays +
                ", type='" + type + '\'' +
                ", destination='" + destination + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", season='" + season + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", maxAltitude=" + maxAltitude +
                ", category='" + category + '\'' +
                ", active=" + active +
                '}';
    }
}
