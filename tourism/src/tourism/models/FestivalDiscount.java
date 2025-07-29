package com.tourism.models;

import java.time.LocalDate;

public class FestivalDiscount {
    private String discountId;
    private String festivalName;
    private double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private String description;

    // Constructors
    public FestivalDiscount() {
        this.active = true;
    }

    public FestivalDiscount(String discountId, String festivalName, double discountPercentage,
                            LocalDate startDate, LocalDate endDate) {
        this.discountId = discountId;
        this.festivalName = festivalName;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    // Getters and Setters
    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getFestivalName() {
        return festivalName;
    }

    public void setFestivalName(String festivalName) {
        this.festivalName = festivalName;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FestivalDiscount{" +
                "discountId='" + discountId + '\'' +
                ", festivalName='" + festivalName + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                '}';
    }
}