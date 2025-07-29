package com.tourism.controllers;

import com.tourism.services.AuthenticationService;
import com.tourism.utils.FileDataManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent; // Import ActionEvent

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private ComboBox<String> languageComboBox; // Make sure this is also declared if not already

    private AuthenticationService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = new AuthenticationService();

        // Initialize ComboBox items (example, adjust as needed)
        if (languageComboBox != null) {
            languageComboBox.getItems().addAll("English", "Nepali"); // Add your languages
            languageComboBox.getSelectionModel().select("English"); // Set default
        }

        setupEventHandlers();
        clearErrorMessage();
    }

    private void setupEventHandlers() {
        if (loginButton != null) {
            loginButton.setOnAction(event -> handleLogin());
        }

        if (passwordField != null) {
            passwordField.setOnAction(event -> handleLogin());
        }

        if (usernameField != null) {
            usernameField.setOnAction(event -> handleLogin());
        }
    }

    // This is the missing method you need to add
    @FXML
    private void handleLanguageChange(ActionEvent event) {
        if (languageComboBox != null) {
            String selectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
            System.out.println("Language changed to: " + selectedLanguage);
            FileDataManager.logActivity("SYSTEM", "Language changed to: " + selectedLanguage);
            // You can add logic here to change the application's language
        }
    }

    @FXML
    private void handleLogin() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            clearErrorMessage();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Please enter both username and password");
                return;
            }

            if (authService.authenticateUser(username, password)) {
                FileDataManager.logActivity(username, "Login successful");
                com.tourism.TourismApp.switchScene("/fxml/dashboard.fxml", "Dashboard");
            } else {
                showError("Invalid username or password");
                passwordField.clear();
            }

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            showError("Login failed. Please try again.");
            FileDataManager.logActivity("SYSTEM", "Login error: " + e.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Forgot Password");
            alert.setHeaderText("Password Reset");
            alert.setContentText("Please contact your system administrator to reset your password.");
            alert.showAndWait();

            FileDataManager.logActivity("SYSTEM", "Forgot password requested");

        } catch (Exception e) {
            System.err.println("Forgot password error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            com.tourism.TourismApp.switchScene("/fxml/tourist-registration.fxml", "Registration");
            FileDataManager.logActivity("SYSTEM", "Registration page accessed");

        } catch (Exception e) {
            System.err.println("Registration navigation error: " + e.getMessage());
            showError("Registration is currently unavailable");
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    private void clearErrorMessage() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void handleClearFields() {
        if (usernameField != null) {
            usernameField.clear();
        }
        if (passwordField != null) {
            passwordField.clear();
        }
        clearErrorMessage();
    }

    @FXML
    private void handleExit() {
        try {
            FileDataManager.logActivity("SYSTEM", "Application exit requested from login");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Exit error: " + e.getMessage());
        }
    }
}
