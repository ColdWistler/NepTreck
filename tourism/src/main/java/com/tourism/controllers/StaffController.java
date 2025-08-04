package com.tourism.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.tourism.models.User;
import com.tourism.utils.SessionManager;

import java.util.Objects;

public class StaffController {

    @FXML private Label staffWelcomeLabel;
    @FXML private Button staffLogoutButton;
    @FXML private Button newBookingButton;
    @FXML private Button emergencyContactsButton;
    @FXML private Button viewAttractionsButton;
    @FXML private Button festivalDiscountButton;
    @FXML private Button backButton;

    public void initialize() {
        setupStaffDashboard();
        setupEventHandlers();
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
        emergencyContactsButton.setOnAction(e -> openEmergencyContacts());
        viewAttractionsButton.setOnAction(e -> openViewAttractions());
        festivalDiscountButton.setOnAction(e -> openFestivalDiscounts());
        backButton.setOnAction(e -> handleBack());
    }

    @FXML
    private void openBookingForm() {
        loadScene("/fxml/booking-form.fxml");
    }

    @FXML
    private void openEmergencyContacts() {
        loadScene("/fxml/emergency-report.fxml"); // Update if needed
    }

    @FXML
    private void openViewAttractions() {
        loadScene("/fxml/view-attractions.fxml");
    }

    @FXML
    private void openFestivalDiscounts() {
        loadScene("/fxml/festival-offers-management-view.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        loadScene("/fxml/login.fxml");
    }

    @FXML
    private void handleBack() {

        loadScene("/fxml/login.fxml");
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());

            Stage stage = (Stage) staffWelcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            showAlert("Failed to load scene: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
