package com.tourism.services;

import com.tourism.models.Tourist;
import com.tourism.utils.FileDataManager;

import java.util.List;
import java.util.stream.Collectors;

public class TouristService {

    public boolean registerTourist(Tourist tourist) {
        try {
            // Generate ID if not provided
            if (tourist.getTouristId() == null || tourist.getTouristId().isEmpty()) {
                tourist.setTouristId(generateTouristId());
            }

            // Generate account ID if not provided
            if (tourist.getAccountId() == null || tourist.getAccountId().isEmpty()) {
                tourist.setAccountId(generateAccountId());
            }

            boolean saved = FileDataManager.saveTourist(tourist);

            if (saved) {
                FileDataManager.logActivity("SYSTEM", "Tourist registered: " + tourist.getFullName());
            }

            return saved;

        } catch (Exception e) {
            System.err.println("Error registering tourist: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Tourist registration error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTourist(Tourist tourist) {
        try {
            boolean updated = FileDataManager.saveTourist(tourist);

            if (updated) {
                FileDataManager.logActivity("SYSTEM", "Tourist updated: " + tourist.getTouristId());
            }

            return updated;

        } catch (Exception e) {
            System.err.println("Error updating tourist: " + e.getMessage());
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
            return List.of();
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

    public boolean deactivateTourist(String touristId) {
        try {
            Tourist tourist = FileDataManager.findTouristById(touristId);
            if (tourist != null) {
                tourist.setActive(false);
                boolean updated = FileDataManager.saveTourist(tourist);

                if (updated) {
                    FileDataManager.logActivity("SYSTEM", "Tourist deactivated: " + touristId);
                }

                return updated;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error deactivating tourist: " + e.getMessage());
            return false;
        }
    }

    public List<Tourist> searchTourists(String searchTerm) {
        try {
            return FileDataManager.getAllTourists().stream()
                    .filter(tourist ->
                            (tourist.getFullName() != null && tourist.getFullName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                                    (tourist.getEmail() != null && tourist.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) ||
                                    (tourist.getNationality() != null && tourist.getNationality().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching tourists: " + e.getMessage());
            return List.of();
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