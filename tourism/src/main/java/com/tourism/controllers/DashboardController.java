package com.tourism.controllers;

import com.tourism.models.FestivalDiscount;
import com.tourism.services.BookingService;
import com.tourism.services.TouristService;
import com.tourism.services.TourPackageService;
import com.tourism.services.GuideService;
import com.tourism.utils.FileDataManager;
import com.tourism.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label totalTouristsLabel;
    @FXML private Label totalPackagesLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalGuidesLabel;
    @FXML private Label activeDiscountsLabel;
    @FXML private VBox dashboardContent;
    @FXML private VBox statsContainer;

    private TouristService touristService;
    private TourPackageService packageService;
    private BookingService bookingService;
    private GuideService guideService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        touristService = new TouristService();
        packageService = new TourPackageService();
        bookingService = new BookingService();
        guideService = new GuideService();

        // Load dashboard data
        loadDashboardData();

        // Show dashboard content
        showDashboard();
    }

    private void loadDashboardData() {
        try {
            // Set welcome message
            if (welcomeLabel != null) {
                String username = SessionManager.getCurrentUser() != null ?
                        SessionManager.getCurrentUser().getFullName() : "User";
                welcomeLabel.setText("Welcome, " + username + "!");
            }

            // Load statistics
            updateStatistics();

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            // Update tourist count
            if (totalTouristsLabel != null) {
                int totalTourists = getTotalTourists();
                totalTouristsLabel.setText(String.valueOf(totalTourists));
            }

            // Update package count
            if (totalPackagesLabel != null) {
                int totalPackages = getTotalPackages();
                totalPackagesLabel.setText(String.valueOf(totalPackages));
            }

            // Update booking count
            if (totalBookingsLabel != null) {
                int totalBookings = getTotalBookings();
                totalBookingsLabel.setText(String.valueOf(totalBookings));
            }

            // Update guide count
            if (totalGuidesLabel != null) {
                int totalGuides = getTotalGuides();
                totalGuidesLabel.setText(String.valueOf(totalGuides));
            }

            // Update active discounts count
            if (activeDiscountsLabel != null) {
                int activeDiscounts = getActiveDiscounts();
                activeDiscountsLabel.setText(String.valueOf(activeDiscounts));
            }

        } catch (Exception e) {
            System.err.println("Error updating statistics: " + e.getMessage());
        }
    }

    private int getTotalTourists() {
        try {
            return touristService.getTotalTouristsCount();
        } catch (Exception e) {
            System.err.println("Error getting total tourists: " + e.getMessage());
            return 0;
        }
    }

    private int getTotalPackages() {
        try {
            return packageService.getTotalPackagesCount();
        } catch (Exception e) {
            System.err.println("Error getting total packages: " + e.getMessage());
            return 0;
        }
    }

    private int getTotalBookings() {
        try {
            return bookingService.getTotalBookingsCount();
        } catch (Exception e) {
            System.err.println("Error getting total bookings: " + e.getMessage());
            return 0;
        }
    }

    private int getTotalGuides() {
        try {
            return guideService.getTotalGuidesCount();
        } catch (Exception e) {
            System.err.println("Error getting total guides: " + e.getMessage());
            return 0;
        }
    }

    private int getActiveDiscounts() {
        try {
            return FileDataManager.getActiveDiscounts().size();
        } catch (Exception e) {
            System.err.println("Error getting active discounts: " + e.getMessage());
            return 0;
        }
    }

    @FXML
    private void showDashboard() {
        try {
            // Show dashboard content
            if (dashboardContent != null) {
                dashboardContent.setVisible(true);
            }

            // Refresh dashboard data
            loadDashboardData();

        } catch (Exception e) {
            System.err.println("Error showing dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshDashboard() {
        try {
            updateStatistics();
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                    "Dashboard refreshed");
        }
        catch (Exception e) {
            System.err.println("Error refreshing dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewTourists() {
        try {
            // Navigate to tourists view
            com.tourism.TourismApp.switchScene("/fxml/tourist-management-view.fxml", "Tourist Management");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                    "Navigated to Tourist Management");
        } catch (Exception e) {
            System.err.println("Error navigating to tourists: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewPackages() {
        try {
            // Navigate to packages view
            com.tourism.TourismApp.switchScene("/fxml/package-management.fxml", "Package Management");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                    "Navigated to Package Management");
        } catch (Exception e) {
            System.err.println("Error navigating to packages: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewBookings() {
        try {
            // Navigate to bookings view
            com.tourism.TourismApp.switchScene("/fxml/booking-management-view.fxml", "Booking Management");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                    "Navigated to Booking Management");
        } catch (Exception e) {
            System.err.println("Error navigating to bookings: " + e.getMessage());
        }
    }

    // New method to handle navigation to Guide Management
    @FXML
    private void handleViewGuides() {
        try {
            // Navigate to guides view
            com.tourism.TourismApp.switchScene("/fxml/guide-management-view.fxml", "Guide Management");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                    "Navigated to Guide Management");
        } catch (Exception e) {
            System.err.println("Error navigating to guides: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateBackup() {
        try {
            boolean success = FileDataManager.createBackup();
            if (success) {
                System.out.println("Backup created successfully!");
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                        "Backup created successfully");
            } else {
                System.err.println("Failed to create backup");
            }
        } catch (Exception e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            String username = SessionManager.getCurrentUser().getUsername();
            SessionManager.clearSession();
            FileDataManager.logActivity(username, "User logged out from dashboard");

            // Navigate back to login
            com.tourism.TourismApp.switchScene("/fxml/login.fxml", "Login");

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }

    public void refreshData() {
        loadDashboardData();
    }

    private void displayRecentActivity() {
        try {
            // This could show recent bookings, registrations, etc.
            // Implementation depends on your UI requirements
            System.out.println("Displaying recent activity...");
        } catch (Exception e) {
            System.err.println("Error displaying recent activity: " + e.getMessage());
        }
    }

    private void displaySystemStatus() {
        try {
            // Show system status information
            long dataSize = FileDataManager.getDataSize();
            System.out.println("System data size: " + dataSize + " bytes");
        } catch (Exception e) {
            System.err.println("Error displaying system status: " + e.getMessage());
        }
    }
}
