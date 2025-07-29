package com.tourism.services;

import com.tourism.models.FestivalDiscount;
import com.tourism.utils.FileDataManager;

import java.util.List;
import java.util.stream.Collectors;

public class FestivalDiscountService {

    public boolean saveDiscount(FestivalDiscount discount) {
        try {
            // Generate ID if not provided (FileDataManager handles update if ID exists)
            if (discount.getDiscountId() == null || discount.getDiscountId().isEmpty()) {
                discount.setDiscountId(generateDiscountId());
            }

            boolean saved = FileDataManager.saveDiscount(discount); // Using FileDataManager's saveDiscount

            if (saved) {
                FileDataManager.logActivity("SYSTEM", "Festival Discount saved: " + discount.getFestivalName());
            }
            return saved;
        } catch (Exception e) {
            System.err.println("Error saving festival discount: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Festival Discount save error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDiscount(String discountId) {
        try {
            boolean deleted = FileDataManager.deleteDiscount(discountId); // Using FileDataManager's deleteDiscount
            if (deleted) {
                FileDataManager.logActivity("SYSTEM", "Festival Discount deleted: " + discountId);
            }
            return deleted;
        } catch (Exception e) {
            System.err.println("Error deleting festival discount: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Festival Discount delete error: " + e.getMessage());
            return false;
        }
    }

    public FestivalDiscount getDiscountById(String discountId) {
        try {
            return FileDataManager.findDiscountById(discountId);
        } catch (Exception e) {
            System.err.println("Error getting discount by ID: " + e.getMessage());
            return null;
        }
    }

    public List<FestivalDiscount> getAllDiscounts() {
        try {
            return FileDataManager.getAllDiscounts();
        } catch (Exception e) {
            System.err.println("Error getting all discounts: " + e.getMessage());
            return List.of();
        }
    }

    public List<FestivalDiscount> getActiveDiscounts() {
        try {
            return FileDataManager.getActiveDiscounts(); // FileDataManager already has this filtered list
        } catch (Exception e) {
            System.err.println("Error getting active discounts: " + e.getMessage());
            return List.of();
        }
    }

    public List<FestivalDiscount> searchDiscounts(String searchTerm) {
        try {
            return FileDataManager.getAllDiscounts().stream()
                    .filter(discount ->
                            (discount.getFestivalName() != null && discount.getFestivalName().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching discounts: " + e.getMessage());
            return List.of();
        }
    }

    public int getTotalDiscountsCount() {
        try {
            return FileDataManager.getAllDiscounts().size();
        } catch (Exception e) {
            System.err.println("Error getting total discounts count: " + e.getMessage());
            return 0;
        }
    }

    private String generateDiscountId() {
        return "DISC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    public boolean validateDiscountData(FestivalDiscount discount) {
        if (discount == null) return false;
        if (discount.getFestivalName() == null || discount.getFestivalName().trim().isEmpty()) return false;
        // Assuming discountPercentage is a primitive double, no null check needed, but check range
        return discount.getDiscountPercentage() >= 0 && discount.getDiscountPercentage() <= 100;
    }
}
