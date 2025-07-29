package com.tourism.models;

import java.time.LocalDateTime;

public class Booking {
    private String bookingId;
    private String touristId;
    private String packageId;
    private String guideId;
    private LocalDateTime bookingDate;
    private LocalDateTime travelDate;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private Double totalAmount;
    private Integer numberOfPeople;
    private String specialRequests;
    private String paymentStatus; // PENDING, PAID, REFUNDED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
    }

    public Booking(String bookingId, String touristId, String packageId, String guideId) {
        this.bookingId = bookingId;
        this.touristId = touristId;
        this.packageId = packageId;
        this.guideId = guideId;
        this.bookingDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTouristId() {
        return touristId;
    }

    public void setTouristId(String touristId) {
        this.touristId = touristId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getGuideId() {
        return guideId;
    }

    public void setGuideId(String guideId) {
        this.guideId = guideId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDateTime travelDate) {
        this.travelDate = travelDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", touristId='" + touristId + '\'' +
                ", packageId='" + packageId + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", numberOfPeople=" + numberOfPeople +
                '}';
    }
}