package com.tourism.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.tourism.models.User;
import com.tourism.utils.SessionManager;

public class StaffController {

    @FXML private Label staffWelcomeLabel;
    @FXML private Button staffLogoutButton;
    @FXML private Button newBookingButton;
    @FXML private Button registerGuideButton;
    @FXML private Button viewReportsButton;
    @FXML private Button manageTouristsButton;
    @FXML private Button emergencyContactsButton;
    @FXML private Button settingsButton;
    @FXML private TableView<String> recentActivityTable;

    public void initialize() {
        setupStaffDashboard();
        setupEventHandlers();
        loadRecentActivity();
    }

    private void setupStaffDashboard() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            staffWelcomeLabel.setText("Welcome, " + currentUser.getUsername());
        }
    }

    private void setupEventHandlers() {
        staffLogoutButton.setOnAction(e -> handleLogout());
        newBookingButton.setOnAction(e -> openBookingForm());
        registerGuideButton.setOnAction(e -> openGuideRegistration());
        viewReportsButton.setOnAction(e -> openReports());
        manageTouristsButton.setOnAction(e -> openTouristManagement());
        emergencyContactsButton.setOnAction(e -> openEmergencyContacts());
        settingsButton.setOnAction(e -> openSettings());
    }

    private void loadRecentActivity() {
        // Load recent activity data
        // This would typically come from a service
    }

    @FXML
    private void openBookingForm() {
        loadScene("/fxml/booking-form.fxml");
    }

    @FXML
    private void openGuideRegistration() {
        loadScene("/fxml/guide-registration.fxml");
    }

    @FXML
    private void openReports() {
        showAlert("Reports", "Reports module will be implemented here.");
    }

    @FXML
    private void openTouristManagement() {
        showAlert("Tourist Management", "Tourist management module will be implemented here.");
    }

    @FXML
    private void openEmergencyContacts() {
        showAlert("Emergency Contacts", "Emergency contacts module will be implemented here.");
    }

    @FXML
    private void openSettings() {
        showAlert("Settings", "Settings module will be implemented here.");
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        loadScene("/fxml/login.fxml");
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = (Stage) staffWelcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            showAlert("Error", "Failed to load scene: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}