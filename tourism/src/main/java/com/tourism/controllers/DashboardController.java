package com.tourism.controllers;

import com.tourism.TourismApp; // Import TourismApp
import com.tourism.models.FestivalDiscount;
import com.tourism.services.BookingService;
import com.tourism.services.TouristService;
import com.tourism.services.TourPackageService;
import com.tourism.services.GuideService;
import com.tourism.utils.FileDataManager;
import com.tourism.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button; // Import Button

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale; // Import Locale

public class DashboardController implements Initializable {
    @FXML
    private BarChart<String, Number> sampleBarChart;

    @FXML private Label welcomeLabel;
    @FXML private Label totalTouristsLabel;
    @FXML private Label totalPackagesLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalGuidesLabel;
    @FXML private Label activeDiscountsLabel;
    @FXML private VBox dashboardContent;
    @FXML private VBox statsContainer;

    // FXML fields for localization
    @FXML private Label headerTitle;
    @FXML private Label adminPanelLabel;
    @FXML private Button logoutButton;
    @FXML private Label navigationLabel;
    @FXML private Button dashboardBtn;
    @FXML private Button bookingManagementBtn;
    @FXML private Button accountManagementBtn;
    @FXML private Button festivalOffersBtn;
    @FXML private Button settingsBtn; // Renamed from settingsBtn to guideManagementBtn in FXML for clarity
    @FXML private Label overviewTitle;
    @FXML private Label totalBookingsTextLabel; // For "Total Bookings" text
    @FXML private Label activeDiscountsTextLabel; // For "Active Discounts" text
    @FXML private Label totalTouristsTextLabel; // For "Total Tourists" text
    @FXML private Label totalPackagesTextLabel; // For "Total Packages" text
    @FXML private Label totalGuidesTextLabel; // For "Total Guides" text
    @FXML private CategoryAxis chartCategoryAxis; // Add for Chart X-axis label
    @FXML private NumberAxis chartValueAxis; // Add for Chart Y-axis label

    private TouristService touristService;
    private TourPackageService packageService;
    private BookingService bookingService;
    private GuideService guideService;
    private ResourceBundle bundle; // Declare ResourceBundle

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        touristService = new TouristService();
        packageService = new TourPackageService();
        bookingService = new BookingService();
        guideService = new GuideService();

        // Load resource bundle
        bundle = ResourceBundle.getBundle("i18n.messages", TourismApp.getLocale()); // Load bundle using TourismApp's locale

        // Apply localized texts
        applyLocalizedTexts();

        // Load dashboard data
        loadDashboardData();

        // Show dashboard content
        showDashboard();

        XYChart.Series<String, Number> tourists = new XYChart.Series<>();
        tourists.setName(bundle.getString("dashboard.chart.touristsSeriesName")); // Localize series name
        tourists.getData().add(new XYChart.Data<>("Jan", 100));
        tourists.getData().add(new XYChart.Data<>("Feb", 130));

        XYChart.Series<String, Number> bookings = new XYChart.Series<>();
        bookings.setName(bundle.getString("dashboard.chart.bookingsSeriesName")); // Localize series name
        bookings.getData().add(new XYChart.Data<>("Jan", 80));
        bookings.getData().add(new XYChart.Data<>("Feb", 120));

        sampleBarChart.getData().addAll(tourists, bookings);
        sampleBarChart.setTitle(bundle.getString("dashboard.chartTitle")); // Localize chart title
    }

    private void applyLocalizedTexts() {
        if (welcomeLabel != null) {
            String username = SessionManager.getCurrentUser() != null ?
                    SessionManager.getCurrentUser().getFullName() : "Admin";
            // Use MessageFormat for dynamic messages if needed, or split the string
            welcomeLabel.setText(bundle.getString("dashboard.welcomeAdmin").replace("Admin", username));
        }

        // Header
        // if (headerTitle != null) headerTitle.setText(bundle.getString("app.brandTitle")); // Assuming you want brand title here
        if (adminPanelLabel != null) adminPanelLabel.setText(bundle.getString("dashboard.adminPanel"));
        if (logoutButton != null) logoutButton.setText(bundle.getString("dashboard.logout"));

        // Sidebar Navigation
        if (navigationLabel != null) navigationLabel.setText(bundle.getString("dashboard.navigation"));
        if (dashboardBtn != null) dashboardBtn.setText(bundle.getString("dashboard.button.dashboard"));
        if (bookingManagementBtn != null) bookingManagementBtn.setText(bundle.getString("dashboard.button.bookingManagement"));
        if (accountManagementBtn != null) accountManagementBtn.setText(bundle.getString("dashboard.button.accountManagement"));
        if (festivalOffersBtn != null) festivalOffersBtn.setText(bundle.getString("dashboard.button.festivalOffers"));
        if (settingsBtn != null) settingsBtn.setText(bundle.getString("dashboard.button.guideManagement")); // Check FXML fx:id

        // Dashboard Overview and Stats
        if (overviewTitle != null) overviewTitle.setText(bundle.getString("dashboard.overview"));
        if (sampleBarChart != null) sampleBarChart.setTitle(bundle.getString("dashboard.chartTitle"));
        if (sampleBarChart.getXAxis() instanceof CategoryAxis) {
            ((CategoryAxis) sampleBarChart.getXAxis()).setLabel(bundle.getString("dashboard.chart.category"));
        }
        if (sampleBarChart.getYAxis() instanceof NumberAxis) {
            ((NumberAxis) sampleBarChart.getYAxis()).setLabel(bundle.getString("dashboard.chart.value"));
        }

        // Stat Card Labels (assuming you create FXML Label fields for these as well)

        if (totalBookingsTextLabel != null) totalBookingsTextLabel.setText(bundle.getString("dashboard.totalBookings"));
        if (activeDiscountsTextLabel != null) activeDiscountsTextLabel.setText(bundle.getString("dashboard.activeDiscounts"));
        if (totalTouristsTextLabel != null) totalTouristsTextLabel.setText(bundle.getString("dashboard.totalTourists"));
        if (totalPackagesTextLabel != null) totalPackagesTextLabel.setText(bundle.getString("dashboard.totalPackages"));
        if (totalGuidesTextLabel != null) totalGuidesTextLabel.setText(bundle.getString("dashboard.totalGuides"));
    }

    private void loadDashboardData() {
        try {
            // Set welcome message - now handled by applyLocalizedTexts for initial load

            if (welcomeLabel != null && SessionManager.getCurrentUser() != null) {
                String username = SessionManager.getCurrentUser().getFullName();
                welcomeLabel.setText(bundle.getString("dashboard.welcomeAdmin").replace("Admin", username));
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
            com.tourism.TourismApp.switchScene("/fxml/tourist-management-view.fxml", bundle.getString("dashboard.button.accountManagement")); // Localize title
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                    "Navigated to Tourist Management");
        } catch (Exception e) {
            System.err.println("Error navigating to tourists: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewPackages() {
        try {
            // NEW: Route to Festival Offers Management
            com.tourism.TourismApp.switchScene("/fxml/festival-offers-management-view.fxml", bundle.getString("dashboard.button.festivalOffers")); // Localize title
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                    "Navigated to Festival Offers Management");
        } catch (Exception e) {
            System.err.println("Error navigating to Festival Offers: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewBookings() {
        try {
            // Navigate to bookings view
            com.tourism.TourismApp.switchScene("/fxml/booking-management-view.fxml", bundle.getString("dashboard.button.bookingManagement")); // Localize title
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
            com.tourism.TourismApp.switchScene("/fxml/guide-management-view.fxml", bundle.getString("dashboard.button.guideManagement")); // Localize title
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
            com.tourism.TourismApp.switchScene("/fxml/login.fxml", bundle.getString("login.title")); // Localize title

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
