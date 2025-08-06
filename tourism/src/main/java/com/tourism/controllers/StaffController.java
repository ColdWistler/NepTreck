package com.tourism.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.tourism.models.User;
import com.tourism.utils.SessionManager;

import java.io.IOException;

public class StaffController {

    @FXML private Label staffWelcomeLabel;
    @FXML private Button staffLogoutButton;
    @FXML private Button newBookingButton;
    @FXML private Button emergencyContactsButton;
    @FXML private Button viewAttractionsButton;
    @FXML private Button festivalDiscountButton;
    @FXML private Button backButton; // Added backButton FXML ID

    @FXML private VBox contentArea; // VBox to hold the loaded content

    public void initialize() {
        setupStaffDashboard();
        setupEventHandlers();
        // Removed the problematic line: loadContent("/fxml/staff-welcome-content.fxml");
        // The contentArea will now be empty initially, showing the default message from FXML.
    }

    private void setupStaffDashboard() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            staffWelcomeLabel.setText("Welcome, " + currentUser.getUsername());
        }
    }

    private void setupEventHandlers() {
        staffLogoutButton.setOnAction(e -> handleLogout());
        newBookingButton.setOnAction(e -> loadContent("/fxml/booking-form.fxml"));
        emergencyContactsButton.setOnAction(e -> loadContent("/fxml/emergency-report.fxml"));
        viewAttractionsButton.setOnAction(e -> loadContent("/fxml/view-attractions.fxml"));
        festivalDiscountButton.setOnAction(e -> loadContent("/fxml/festival-offers-management-view.fxml"));
        if (backButton != null) { // Added handler for backButton
            backButton.setOnAction(e -> handleBackToLogin());
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        // Switch to login scene
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) staffLogoutButton.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Failed to load login scene: " + e.getMessage());
        }
    }

    // New method to handle "Back to Login" button
    @FXML
    private void handleBackToLogin() {
        SessionManager.clearSession(); // Clear session on going back to login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow(); // Use backButton to get stage
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Failed to load login scene: " + e.getMessage());
        }
    }


    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            showAlert("Failed to load scene: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
