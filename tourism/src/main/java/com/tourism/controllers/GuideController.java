package com.tourism.controllers;

import com.tourism.models.Guide;
import com.tourism.services.GuideService; // Import GuideService
import com.tourism.utils.FileDataManager; // For logging
import com.tourism.utils.SessionManager; // For logging username

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional; // For Alert.showAndWait()
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

    // Corrected TableView and TableColumn types
    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> guideIdColumn;
    @FXML private TableColumn<Guide, String> fullNameColumn;
    @FXML private TableColumn<Guide, String> emailColumn;
    @FXML private TableColumn<Guide, String> phoneNumberColumn;
    @FXML private TableColumn<Guide, String> specializationColumn;
    @FXML private TableColumn<Guide, Boolean> activeColumn; // Boolean for CheckBox

    @FXML private Button refreshTableButton;

    private GuideService guideService; // Declare GuideService
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
            clearFormButton.setOnAction(event -> handleClearForm());
        }
        if (refreshTableButton != null) {
            refreshTableButton.setOnAction(event -> handleRefreshTable());
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
            e.printStackTrace(); // Print full stack trace for detailed errors
            showStatus("Error loading guides: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleCreateGuide() {
        System.out.println("DEBUG: handleCreateGuide called.");
        try {
            // Basic validation
            if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty() || specializationField.getText().isEmpty()) {
                showStatus("Full Name, Email, and Specialization are required.", false);
                return;
            }

            // Create a new Guide object
            // GuideService handles ID generation
            Guide newGuide = new Guide();
            newGuide.setFullName(fullNameField.getText());
            newGuide.setEmail(emailField.getText());
            newGuide.setPhoneNumber(phoneNumberField.getText());
            newGuide.setSpecialization(specializationField.getText());
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

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = guideService.deleteGuide(selectedGuide.getGuideId()); // Call deleteGuide

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
        if (guidesTable != null) { // Clear selection if table exists
            guidesTable.getSelectionModel().clearSelection();
        }
        clearStatusMessage();
    }

    @FXML
    private void handleRefreshTable() {
        System.out.println("DEBUG: handleRefreshTable called.");
        loadGuides();
        showStatus("Guide data refreshed.", true);
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
