package com.tourism.services;

import com.tourism.models.Tourist;
import com.tourism.utils.FileDataManager;

import java.util.List;
import java.util.stream.Collectors;

public class TouristService {

    // This method will handle both creating new tourists and updating existing ones.
    // It directly calls FileDataManager.saveTourist which should have the logic to overwrite if ID exists.
    public boolean saveTourist(Tourist tourist) { // Renamed from registerTourist/updateTourist for controller
        try {
            // Generate ID if not provided (for new tourists)
            if (tourist.getTouristId() == null || tourist.getTouristId().isEmpty()) {
                tourist.setTouristId(generateTouristId());
            }

            // Generate account ID if not provided (for new tourists)
            if (tourist.getAccountId() == null || tourist.getAccountId().isEmpty()) {
                tourist.setAccountId(generateAccountId());
            }

            boolean saved = FileDataManager.saveTourist(tourist); // FileDataManager handles add/update logic

            if (saved) {
                FileDataManager.logActivity("SYSTEM", "Tourist saved/updated: " + tourist.getFullName());
            }

            return saved;

        } catch (Exception e) {
            System.err.println("Error saving/updating tourist: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Tourist save/update error: " + e.getMessage());
            return false;
        }
    }

    public Tourist getTouristById(String touristId) {
        try {
            return FileDataManager.findTouristById(touristId);
        } catch (Exception e) {
            System.err.println("Error getting tourist by ID: " + e.getMessage());
            return null;
        }
    }

    public List<Tourist> getAllTourists() {
        try {
            return FileDataManager.getAllTourists();
        } catch (Exception e) {
            System.err.println("Error getting all tourists: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    public List<Tourist> getActiveTourists() {
        try {
            return FileDataManager.getAllTourists().stream()
                    .filter(Tourist::isActive)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting active tourists: " + e.getMessage());
            return List.of();
        }
    }

    // Method to handle deleting a tourist
    public boolean deleteTourist(String touristId) { // Added this method for TouristController
        try {
            boolean deleted = FileDataManager.deleteTourist(touristId); // Assuming FileDataManager has this method
            if (deleted) {
                FileDataManager.logActivity("SYSTEM", "Tourist deleted: " + touristId);
            }
            return deleted;
        } catch (Exception e) {
            System.err.println("Error deleting tourist: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Tourist deletion error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalTouristsCount() {
        try {
            return FileDataManager.getAllTourists().size();
        } catch (Exception e) {
            System.err.println("Error getting total tourists count: " + e.getMessage());
            return 0;
        }
    }

    public int getActiveTouristsCount() {
        try {
            return getActiveTourists().size();
        } catch (Exception e) {
            System.err.println("Error getting active tourists count: " + e.getMessage());
            return 0;
        }
    }

    private String generateTouristId() {
        return "T" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private String generateAccountId() {
        return "ACC_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    public boolean validateTouristData(Tourist tourist) {
        if (tourist == null) return false;
        if (tourist.getFullName() == null || tourist.getFullName().trim().isEmpty()) return false;
        if (tourist.getEmail() == null || tourist.getEmail().trim().isEmpty()) return false;
        if (tourist.getPhoneNumber() == null || tourist.getPhoneNumber().trim().isEmpty()) return false;

        return true;
    }
}
