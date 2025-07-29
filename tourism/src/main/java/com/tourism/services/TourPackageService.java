package com.tourism.services;

import com.tourism.models.TourPackage;
import com.tourism.utils.FileDataManager;

import java.util.List;
import java.util.stream.Collectors;

public class TourPackageService {

    public boolean createPackage(TourPackage tourPackage) {
        try {
            // Generate ID if not provided
            if (tourPackage.getPackageId() == null || tourPackage.getPackageId().isEmpty()) {
                tourPackage.setPackageId(generatePackageId());
            }

            List<TourPackage> packages = FileDataManager.getAllPackages();
            packages.add(tourPackage);

            boolean saved = FileDataManager.savePackages(packages);

            if (saved) {
                FileDataManager.logActivity("SYSTEM", "Package created: " + tourPackage.getPackageName());
            }

            return saved;

        } catch (Exception e) {
            System.err.println("Error creating package: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Package creation error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePackage(TourPackage tourPackage) {
        try {
            List<TourPackage> packages = FileDataManager.getAllPackages();
            packages.removeIf(p -> tourPackage.getPackageId().equals(p.getPackageId()));
            packages.add(tourPackage);

            boolean updated = FileDataManager.savePackages(packages);

            if (updated) {
                FileDataManager.logActivity("SYSTEM", "Package updated: " + tourPackage.getPackageId());
            }

            return updated;

        } catch (Exception e) {
            System.err.println("Error updating package: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePackage(String packageId) {
        try {
            List<TourPackage> packages = FileDataManager.getAllPackages();
            boolean removed = packages.removeIf(p -> packageId.equals(p.getPackageId()));

            if (removed) {
                boolean saved = FileDataManager.savePackages(packages);

                if (saved) {
                    FileDataManager.logActivity("SYSTEM", "Package deleted: " + packageId);
                }

                return saved;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Error deleting package: " + e.getMessage());
            return false;
        }
    }

    public TourPackage getPackageById(String packageId) {
        try {
            return FileDataManager.findTourPackageById(packageId);
        } catch (Exception e) {
            System.err.println("Error getting package by ID: " + e.getMessage());
            return null;
        }
    }

    public List<TourPackage> getAllPackages() {
        try {
            return FileDataManager.getAllPackages();
        } catch (Exception e) {
            System.err.println("Error getting all packages: " + e.getMessage());
            return List.of();
        }
    }

    public List<TourPackage> getActivePackages() {
        try {
            return FileDataManager.getAllPackages().stream()
                    .filter(TourPackage::isActive)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting active packages: " + e.getMessage());
            return List.of();
        }
    }

    public List<TourPackage> getPackagesByCategory(String category) {
        try {
            return FileDataManager.getAllPackages().stream()
                    .filter(pkg -> category.equalsIgnoreCase(pkg.getCategory()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting packages by category: " + e.getMessage());
            return List.of();
        }
    }

    public List<TourPackage> searchPackages(String searchTerm) {
        try {
            return FileDataManager.getAllPackages().stream()
                    .filter(pkg ->
                            (pkg.getPackageName() != null && pkg.getPackageName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                                    (pkg.getDescription() != null && pkg.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) ||
                                    (pkg.getCategory() != null && pkg.getCategory().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching packages: " + e.getMessage());
            return List.of();
        }
    }

    public int getTotalPackagesCount() {
        try {
            return FileDataManager.getAllPackages().size();
        } catch (Exception e) {
            System.err.println("Error getting total packages count: " + e.getMessage());
            return 0;
        }
    }

    public int getActivePackagesCount() {
        try {
            return getActivePackages().size();
        } catch (Exception e) {
            System.err.println("Error getting active packages count: " + e.getMessage());
            return 0;
        }
    }

    private String generatePackageId() {
        return "PKG" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    public boolean validatePackageData(TourPackage tourPackage) {
        if (tourPackage == null) return false;
        if (tourPackage.getPackageName() == null || tourPackage.getPackageName().trim().isEmpty()) return false;
        if (tourPackage.getDescription() == null || tourPackage.getDescription().trim().isEmpty()) return false;
        if (tourPackage.getPrice() == null || tourPackage.getPrice() <= 0) return false;
        if (tourPackage.getDuration() == null || tourPackage.getDuration() <= 0) return false;

        return true;
    }
}