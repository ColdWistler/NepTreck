package com.tourism.controllers;

import com.tourism.TourismApp;
import com.tourism.services.AuthenticationService;
import com.tourism.utils.FileDataManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert; // <--- ADD THIS IMPORT

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;


    @FXML
    private Label loginTitle;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label brandTitleLabel;
    @FXML
    private Label brandSubtitleLabel;
    @FXML
    private Button forgotPasswordButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button clearFieldsButton;
    @FXML
    private Button exitButton;

    private AuthenticationService authService;
    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = new AuthenticationService();

        bundle = ResourceBundle.getBundle("i18n.messages", TourismApp.getLocale());

        languageComboBox.setItems(FXCollections.observableArrayList("English", "नेपाली (Nepali)"));
        if (TourismApp.getLocale().getLanguage().equals("ne")) {
            languageComboBox.getSelectionModel().select("नेपाली (Nepali)");
        } else {
            languageComboBox.getSelectionModel().select("English");
        }

        setupEventHandlers();
        clearErrorMessage();
        applyLocalizedTexts();
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

    private void applyLocalizedTexts() {
        if (loginTitle != null) loginTitle.setText(bundle.getString("login.title"));
        if (usernameLabel != null) usernameLabel.setText(bundle.getString("login.username"));
        if (passwordLabel != null) passwordLabel.setText(bundle.getString("login.password"));
        if (languageLabel != null) languageLabel.setText(bundle.getString("login.language"));
        if (loginButton != null) loginButton.setText(bundle.getString("login.button.login"));
        if (forgotPasswordButton != null) forgotPasswordButton.setText(bundle.getString("login.button.forgotPassword"));
        if (registerButton != null) registerButton.setText(bundle.getString("login.button.register"));
        if (clearFieldsButton != null) clearFieldsButton.setText(bundle.getString("login.button.clearFields"));
        if (exitButton != null) exitButton.setText(bundle.getString("login.button.exit"));

        if (usernameField != null) usernameField.setPromptText(bundle.getString("login.prompt.username"));
        if (passwordField != null) passwordField.setPromptText(bundle.getString("login.prompt.password"));

        if (brandTitleLabel != null) brandTitleLabel.setText(bundle.getString("app.brandTitle"));
        if (brandSubtitleLabel != null) brandSubtitleLabel.setText(bundle.getString("app.brandSubtitle"));

        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLanguageChange() {
        String selectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
        Locale newLocale;
        if ("नेपाली (Nepali)".equals(selectedLanguage)) {
            newLocale = new Locale("ne");
        } else {
            newLocale = Locale.ENGLISH;
        }

        if (!TourismApp.getLocale().equals(newLocale)) {
            TourismApp.setLocale(newLocale);
            FileDataManager.logActivity("SYSTEM", "Language changed to: " + newLocale.getDisplayName());
        }
    }


    @FXML
    private void handleLogin() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            clearErrorMessage();

            if (username.isEmpty() || password.isEmpty()) {
                showError(bundle.getString("login.error.invalidCredentials"));
                return;
            }

            // Get the authenticated user object
            var user = authService.getUserIfAuthenticated(username, password);

            if (user != null) {
                FileDataManager.logActivity(username, "Login successful");

                // Route based on user role
                if (user.isAdmin()) {
                    TourismApp.switchScene("/fxml/dashboard.fxml", "Admin Dashboard");
                } else if (user.isStaff()) {
                    TourismApp.switchScene("/fxml/staff-home.fxml", "Staff Dashboard");
                }

            } else {
                showError(bundle.getString("login.error.invalidCredentials"));
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
            alert.setTitle(bundle.getString("login.button.forgotPassword"));
            alert.setHeaderText(bundle.getString("login.forgotPassword.header"));
            alert.setContentText(bundle.getString("login.forgotPassword.content"));
            alert.showAndWait();

            FileDataManager.logActivity("SYSTEM", "Forgot password requested");

        } catch (Exception e) {
            System.err.println("Forgot password error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            TourismApp.switchScene("/fxml/tourist-registration.fxml", bundle.getString("login.button.register"));
            FileDataManager.logActivity("SYSTEM", "Registration page accessed");

        } catch (Exception e) {
            System.err.println("Registration navigation error: " + e.getMessage());
            showError(bundle.getString("login.error.registerUnavailable"));
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
