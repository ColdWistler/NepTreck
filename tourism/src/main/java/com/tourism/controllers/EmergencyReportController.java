package com.tourism.controllers;

import com.tourism.utils.FileDataManager;
import com.tourism.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class EmergencyReportController {

    @FXML
    private TextArea emergencyDetails;

    @FXML
    private void handleSubmitEmergency() {
        String message = emergencyDetails.getText().trim();
        if (message.isEmpty()) {
            showAlert("Validation Error", "Please enter emergency details.");
            return;
        }

        String username = SessionManager.getCurrentUser().getUsername();
        FileDataManager.logActivity(username, "Emergency reported: " + message);

        showAlert("Success", "Emergency reported successfully.");
        emergencyDetails.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
