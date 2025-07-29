package com.tourism.controllers;

import com.tourism.models.Guide;
import com.tourism.services.GuideService;
import com.tourism.utils.FileDataManager; // Needed for logActivity
import com.tourism.utils.SessionManager; // Needed for logActivity username

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuideController implements Initializable {
    // FXML elements from guide-management-view.fxml
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField specializationField;
    @FXML private CheckBox activeCheckBox;
    @FXML private Button createGuideButton;
    @FXML private Button updateGuideButton;
    @FXML private Button deleteGuideButton;
    @FXML private Button clearFormButton;
    @FXML private Label statusLabel;
    @FXML private TableView<Guide> guidesTable; // Corrected to specific type
    @FXML private TableColumn<Guide, String> guideIdColumn; // Corrected to specific type
    @FXML private TableColumn<Guide, String> fullNameColumn;
    @FXML private TableColumn<Guide, String> emailColumn;
    @FXML private TableColumn<Guide, String> phoneNumberColumn;
    @FXML private TableColumn<Guide, String> specializationColumn;
    @FXML private TableColumn<Guide, Boolean> activeColumn; // Corrected to Boolean for CheckBox
    @FXML private Button refreshTableButton;

    @FXML private Button backButton; // NEW: Back button FXML element

    private GuideService guideService;
    private ObservableList<Guide> guidesList; // ObservableList for the TableView

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DEBUG: GuideController initializing...");
        guideService = new GuideService(); // Initialize the service

        setupTable(); // Setup columns and bind ObservableList
        loadGuides(); // Load data into the table
        setupEventHandlers(); // Setup button actions and table selection listener

        clearStatusMessage();
        System.out.println("DEBUG: GuideController initialized.");
    }

    private void setupTable() {
        System.out.println("DEBUG: Setting up Guide table columns...");
        // Set up cell value factories for each column
        if (guideIdColumn != null) {
            guideIdColumn.setCellValueFactory(new PropertyValueFactory<>("guideId"));
        }
        if (fullNameColumn != null) {
            fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        }
        if (emailColumn != null) {
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        }
        if (phoneNumberColumn != null) {
            phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        }
        if (specializationColumn != null) {
            specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        }
        if (activeColumn != null) {
            activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        }

        guidesList = FXCollections.observableArrayList();
        if (guidesTable != null) {
            guidesTable.setItems(guidesList); // Bind the ObservableList to the table
            System.out.println("DEBUG: Guides table items set to ObservableList.");
        } else {
            System.err.println("ERROR: guidesTable is null during setupTable! Check FXML fx:id.");
        }
        System.out.println("DEBUG: Guide Table setup complete.");
    }

    private void setupEventHandlers() {
        System.out.println("DEBUG: Setting up Guide event handlers...");
        if (createGuideButton != null) {
            createGuideButton.setOnAction(event -> handleCreateGuide());
        }
        if (updateGuideButton != null) {
            updateGuideButton.setOnAction(event -> handleUpdateGuide());
        }
        if (deleteGuideButton != null) {
            deleteGuideButton.setOnAction(event -> handleDeleteGuide());
        }
        if (clearFormButton != null) {
            clearFormButton.setOnAction(event -> clearForm());
        }
        if (refreshTableButton != null) {
            refreshTableButton.setOnAction(event -> handleRefreshTable());
        }
        if (backButton != null) { // NEW: Set action for back button
            backButton.setOnAction(event -> handleBackToDashboard());
        }


        // Add listener for table row selection to populate the form
        if (guidesTable != null) {
            guidesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateFormWithGuide(newValue);
                    }
                }
            );
        }
        System.out.println("DEBUG: Guide Event handlers setup complete.");
    }

    private void loadGuides() {
        try {
            System.out.println("DEBUG: Attempting to load guides into table...");
            List<Guide> guides = guideService.getAllGuides(); // Get all guides from the service
            System.out.println("DEBUG: Guides retrieved from service: " + (guides != null ? guides.size() : "null") + " items.");

            if (guidesList != null) {
                guidesList.clear(); // Clear existing items
                if (guides != null) {
                    guidesList.addAll(guides); // Add new items
                }
                System.out.println("DEBUG: Guide ObservableList updated. Current size: " + guidesList.size());
            } else {
                System.err.println("ERROR: guidesList is null! Cannot update guide table.");
            }

        } catch (Exception e) {
            System.err.println("Error loading guides for table: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error loading guides: " + e.getMessage(), false);
        }
    }

    // Placeholder methods for form actions
    @FXML
    private void handleCreateGuide() {
        System.out.println("DEBUG: handleCreateGuide called.");
        try {
            // Basic validation
            if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneNumberField.getText().isEmpty()) {
                showStatus("Full Name, Email, and Phone Number are required.", false);
                return;
            }

            // Create a new Guide object (assuming IDs are generated by service or FileDataManager)
            String newId = "G" + System.currentTimeMillis(); // Simple ID generation for now
            Guide newGuide = new Guide(newId, fullNameField.getText(), emailField.getText(),
                                             phoneNumberField.getText(), specializationField.getText());
            newGuide.setActive(activeCheckBox.isSelected());

            boolean success = guideService.registerGuide(newGuide); // Use registerGuide for new

            if (success) {
                showStatus("Guide added successfully!", true);
                clearForm();
                loadGuides(); // Refresh table
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Created guide: " + newGuide.getFullName());
            } else {
                showStatus("Failed to add guide.", false);
            }
        } catch (Exception e) {
            System.err.println("Error creating guide: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error creating guide: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleUpdateGuide() {
        System.out.println("DEBUG: handleUpdateGuide called.");
        Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showStatus("Please select a guide to update.", false);
            return;
        }

        try {
            // Update selected guide object with form data
            selectedGuide.setFullName(fullNameField.getText());
            selectedGuide.setEmail(emailField.getText());
            selectedGuide.setPhoneNumber(phoneNumberField.getText());
            selectedGuide.setSpecialization(specializationField.getText());
            selectedGuide.setActive(activeCheckBox.isSelected());

            boolean success = guideService.updateGuide(selectedGuide); // Use updateGuide

            if (success) {
                showStatus("Guide updated successfully!", true);
                clearForm();
                loadGuides(); // Refresh table
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Updated guide: " + selectedGuide.getFullName());
            } else {
                showStatus("Failed to update guide.", false);
            }
        } catch (Exception e) {
            System.err.println("Error updating guide: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error updating guide: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleDeleteGuide() {
        System.out.println("DEBUG: handleDeleteGuide called.");
        Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showStatus("Please select a guide to delete.", false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Guide");
        alert.setContentText("Are you sure you want to delete this guide: " + selectedGuide.getFullName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = guideService.deleteGuide(selectedGuide.getGuideId());
                if (success) {
                    showStatus("Guide deleted successfully!", true);
                    clearForm();
                    loadGuides(); // Refresh table
                    FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Deleted guide: " + selectedGuide.getFullName());
                } else {
                    showStatus("Failed to delete guide.", false);
                }
            } catch (Exception e) {
                System.err.println("Error deleting guide: " + e.getMessage());
                e.printStackTrace();
                showStatus("Error deleting guide: " + e.getMessage(), false);
            }
        }
    }

    @FXML
    private void handleClearForm() {
        System.out.println("DEBUG: handleClearForm called.");
        clearForm();
        guidesTable.getSelectionModel().clearSelection(); // Deselect table row
        clearStatusMessage();
    }

    @FXML
    private void handleRefreshTable() {
        System.out.println("DEBUG: handleRefreshTable called.");
        loadGuides();
        showStatus("Guide data refreshed.", true);
    }

    // NEW: Method to handle returning to the Dashboard
    @FXML
    private void handleBackToDashboard() {
        try {
            System.out.println("DEBUG: Navigating back to Dashboard...");
            com.tourism.TourismApp.switchScene("/fxml/dashboard.fxml", "Dashboard");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Navigated back to Dashboard from Guide Management");
        } catch (Exception e) {
            System.err.println("Error navigating back to Dashboard: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error navigating back: " + e.getMessage(), false);
        }
    }

    private void populateFormWithGuide(Guide guide) {
        if (guide != null) {
            fullNameField.setText(guide.getFullName());
            emailField.setText(guide.getEmail());
            phoneNumberField.setText(guide.getPhoneNumber());
            specializationField.setText(guide.getSpecialization());
            activeCheckBox.setSelected(guide.isActive());
        }
    }

    private void clearForm() {
        if (fullNameField != null) fullNameField.clear();
        if (emailField != null) emailField.clear();
        if (phoneNumberField != null) phoneNumberField.clear();
        if (specializationField != null) specializationField.clear();
        if (activeCheckBox != null) activeCheckBox.setSelected(false);
    }

    private void showStatus(String message, boolean isSuccess) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isSuccess ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        } else {
            System.err.println("ERROR: statusLabel is null. Cannot display status: " + message);
        }
    }

    private void clearStatusMessage() {
        if (statusLabel != null) {
            statusLabel.setText("");
        }
    }
}
