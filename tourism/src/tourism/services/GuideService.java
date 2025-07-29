package com.tourism.services;

import com.tourism.models.Guide;
import com.tourism.utils.FileDataManager;

import java.util.List;
import java.util.stream.Collectors;

public class GuideService {

    public boolean registerGuide(Guide guide) {
        try {
            // Generate ID if not provided
            if (guide.getGuideId() == null || guide.getGuideId().isEmpty()) {
                guide.setGuideId(generateGuideId());
            }

            // Delegate to FileDataManager's saveGuide, which handles both add/update logic
            boolean saved = FileDataManager.saveGuide(guide);

            if (saved) {
                FileDataManager.logActivity("SYSTEM", "Guide registered: " + guide.getFullName());
            }

            return saved;

        } catch (Exception e) {
            System.err.println("Error registering guide: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Guide registration error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateGuide(Guide guide) {
        try {
            // Delegate to FileDataManager's saveGuide, which handles both add/update logic
            boolean updated = FileDataManager.saveGuide(guide);

            if (updated) {
                FileDataManager.logActivity("SYSTEM", "Guide updated: " + guide.getGuideId());
            }

            return updated;

        } catch (Exception e) {
            System.err.println("Error updating guide: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Guide update error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGuide(String guideId) {
        try {
            // Delegate directly to FileDataManager's deleteGuide method
            boolean deleted = FileDataManager.deleteGuide(guideId);

            if (deleted) {
                FileDataManager.logActivity("SYSTEM", "Guide deleted: " + guideId);
            }

            return deleted;

        } catch (Exception e) {
            System.err.println("Error deleting guide: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Guide deletion error: " + e.getMessage());
            return false;
        }
    }

    public Guide getGuideById(String guideId) {
        try {
            return FileDataManager.findGuideById(guideId);
        } catch (Exception e) {
            System.err.println("Error getting guide by ID: " + e.getMessage());
            return null;
        }
    }

    public List<Guide> getAllGuides() {
        try {
            return FileDataManager.getAllGuides();
        } catch (Exception e) {
            System.err.println("Error getting all guides: " + e.getMessage());
            return List.of();
        }
    }

    public List<Guide> getActiveGuides() {
        try {
            return FileDataManager.getAllGuides().stream()
                    .filter(Guide::isActive)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting active guides: " + e.getMessage());
            return List.of();
        }
    }

    public List<Guide> getGuidesBySpecialization(String specialization) {
        try {
            return FileDataManager.getAllGuides().stream()
                    .filter(guide -> specialization.equalsIgnoreCase(guide.getSpecialization()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting guides by specialization: " + e.getMessage());
            return List.of();
        }
    }

    public boolean deactivateGuide(String guideId) {
        try {
            Guide guide = FileDataManager.findGuideById(guideId);
            if (guide != null) {
                guide.setActive(false);
                return updateGuide(guide); // Use the service's own update method
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error deactivating guide: " + e.getMessage());
            return false;
        }
    }

    public List<Guide> searchGuides(String searchTerm) {
        try {
            return FileDataManager.getAllGuides().stream()
                    .filter(guide ->
                            (guide.getFullName() != null && guide.getFullName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                                    (guide.getEmail() != null && guide.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) ||
                                    (guide.getSpecialization() != null && guide.getSpecialization().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching guides: " + e.getMessage());
            return List.of();
        }
    }

    public int getTotalGuidesCount() {
        try {
            return FileDataManager.getAllGuides().size();
        } catch (Exception e) {
            System.err.println("Error getting total guides count: " + e.getMessage());
            return 0;
        }
    }

    public int getActiveGuidesCount() {
        try {
            return getActiveGuides().size();
        } catch (Exception e) {
            System.err.println("Error getting active guides count: " + e.getMessage());
            return 0;
        }
    }

    private String generateGuideId() {
        return "G" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    public boolean validateGuideData(Guide guide) {
        if (guide == null) return false;
        if (guide.getFullName() == null || guide.getFullName().trim().isEmpty()) return false;
        if (guide.getEmail() == null || guide.getEmail().trim().isEmpty()) return false;
        if (guide.getPhoneNumber() == null || guide.getPhoneNumber().trim().isEmpty()) return false;

        return true;
    }
}
