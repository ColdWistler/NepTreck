package com.tourism.controllers;

import com.tourism.models.Tourist;
import com.tourism.services.TouristService;
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

public class TouristController implements Initializable {

    // FXML elements from tourist-management-view.fxml
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField nationalityField;
    @FXML private CheckBox activeCheckBox;
    @FXML private Button createTouristButton;
    @FXML private Button updateTouristButton;
    @FXML private Button deleteTouristButton;
    @FXML private Button clearFormButton;
    @FXML private Label statusLabel;
    @FXML private TableView<Tourist> touristsTable; // Corrected to specific type
    @FXML private TableColumn<Tourist, String> touristIdColumn; // Corrected to specific type
    @FXML private TableColumn<Tourist, String> fullNameColumn;
    @FXML private TableColumn<Tourist, String> emailColumn;
    @FXML private TableColumn<Tourist, String> phoneNumberColumn;
    @FXML private TableColumn<Tourist, String> nationalityColumn;
    @FXML private TableColumn<Tourist, Boolean> activeColumn; // Corrected to Boolean for CheckBox
    @FXML private Button refreshTableButton;

    @FXML private Button backButton; // NEW: Back button FXML element

    private TouristService touristService;
    private ObservableList<Tourist> touristsList; // ObservableList for the TableView

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DEBUG: TouristController initializing...");
        touristService = new TouristService(); // Initialize the service

        setupTable(); // Setup columns and bind ObservableList
        loadTourists(); // Load data into the table
        setupEventHandlers(); // Setup button actions and table selection listener

        clearStatusMessage();
        System.out.println("DEBUG: TouristController initialized.");
    }

    private void setupTable() {
        System.out.println("DEBUG: Setting up Tourist table columns...");
        // Set up cell value factories for each column
        if (touristIdColumn != null) {
            touristIdColumn.setCellValueFactory(new PropertyValueFactory<>("touristId"));
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
        if (nationalityColumn != null) {
            nationalityColumn.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        }
        if (activeColumn != null) {
            activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        }

        touristsList = FXCollections.observableArrayList();
        if (touristsTable != null) {
            touristsTable.setItems(touristsList); // Bind the ObservableList to the table
            System.out.println("DEBUG: Tourists table items set to ObservableList.");
        } else {
            System.err.println("ERROR: touristsTable is null during setupTable! Check FXML fx:id.");
        }
        System.out.println("DEBUG: Tourist Table setup complete.");
    }

    private void setupEventHandlers() {
        System.out.println("DEBUG: Setting up Tourist event handlers...");
        if (createTouristButton != null) {
            createTouristButton.setOnAction(event -> handleCreateTourist());
        }
        if (updateTouristButton != null) {
            updateTouristButton.setOnAction(event -> handleUpdateTourist());
        }
        if (deleteTouristButton != null) {
            deleteTouristButton.setOnAction(event -> handleDeleteTourist());
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
        if (touristsTable != null) {
            touristsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateFormWithTourist(newValue);
                    }
                }
            );
        }
        System.out.println("DEBUG: Tourist Event handlers setup complete.");
    }

    private void loadTourists() {
        try {
            System.out.println("DEBUG: Attempting to load tourists into table...");
            List<Tourist> tourists = touristService.getAllTourists(); // Get all tourists from the service
            System.out.println("DEBUG: Tourists retrieved from service: " + (tourists != null ? tourists.size() : "null") + " items.");

            if (touristsList != null) {
                touristsList.clear(); // Clear existing items
                if (tourists != null) {
                    touristsList.addAll(tourists); // Add new items
                }
                System.out.println("DEBUG: Tourist ObservableList updated. Current size: " + touristsList.size());
            } else {
                System.err.println("ERROR: touristsList is null! Cannot update tourist table.");
            }

        } catch (Exception e) {
            System.err.println("Error loading tourists for table: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error loading tourists: " + e.getMessage(), false);
        }
    }

    // Placeholder methods for form actions
    @FXML
    private void handleCreateTourist() {
        System.out.println("DEBUG: handleCreateTourist called.");
        try {
            // Basic validation
            if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneNumberField.getText().isEmpty()) {
                showStatus("Full Name, Email, and Phone Number are required.", false);
                return;
            }

            // Create a new Tourist object (assuming IDs are generated by service or FileDataManager)
            String newId = "TR" + System.currentTimeMillis(); // Simple ID generation for now
            Tourist newTourist = new Tourist(newId, null, fullNameField.getText(), emailField.getText(),
                                             phoneNumberField.getText(), nationalityField.getText());
            newTourist.setActive(activeCheckBox.isSelected());

            boolean success = touristService.saveTourist(newTourist); // Use saveTourist for new and update

            if (success) {
                showStatus("Tourist added successfully!", true);
                clearForm();
                loadTourists(); // Refresh table
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Created tourist: " + newTourist.getFullName());
            } else {
                showStatus("Failed to add tourist.", false);
            }
        } catch (Exception e) {
            System.err.println("Error creating tourist: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error creating tourist: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleUpdateTourist() {
        System.out.println("DEBUG: handleUpdateTourist called.");
        Tourist selectedTourist = touristsTable.getSelectionModel().getSelectedItem();
        if (selectedTourist == null) {
            showStatus("Please select a tourist to update.", false);
            return;
        }

        try {
            // Update selected tourist object with form data
            selectedTourist.setFullName(fullNameField.getText());
            selectedTourist.setEmail(emailField.getText());
            selectedTourist.setPhoneNumber(phoneNumberField.getText());
            selectedTourist.setNationality(nationalityField.getText());
            selectedTourist.setActive(activeCheckBox.isSelected());

            boolean success = touristService.saveTourist(selectedTourist); // Use saveTourist for new and update

            if (success) {
                showStatus("Tourist updated successfully!", true);
                clearForm();
                loadTourists(); // Refresh table
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Updated tourist: " + selectedTourist.getFullName());
            } else {
                showStatus("Failed to update tourist.", false);
            }
        } catch (Exception e) {
            System.err.println("Error updating tourist: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error updating tourist: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleDeleteTourist() {
        System.out.println("DEBUG: handleDeleteTourist called.");
        Tourist selectedTourist = touristsTable.getSelectionModel().getSelectedItem();
        if (selectedTourist == null) {
            showStatus("Please select a tourist to delete.", false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Tourist");
        alert.setContentText("Are you sure you want to delete this tourist: " + selectedTourist.getFullName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = touristService.deleteTourist(selectedTourist.getTouristId());
                if (success) {
                    showStatus("Tourist deleted successfully!", true);
                    clearForm();
                    loadTourists(); // Refresh table
                    FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Deleted tourist: " + selectedTourist.getFullName());
                } else {
                    showStatus("Failed to delete tourist.", false);
                }
            } catch (Exception e) {
                System.err.println("Error deleting tourist: " + e.getMessage());
                e.printStackTrace();
                showStatus("Error deleting tourist: " + e.getMessage(), false);
            }
        }
    }

    @FXML
    private void handleClearForm() {
        System.out.println("DEBUG: handleClearForm called.");
        clearForm();
        touristsTable.getSelectionModel().clearSelection(); // Deselect table row
        clearStatusMessage();
    }

    @FXML
    private void handleRefreshTable() {
        System.out.println("DEBUG: handleRefreshTable called.");
        loadTourists();
        showStatus("Tourist data refreshed.", true);
    }

    // NEW: Method to handle returning to the Dashboard
    @FXML
    private void handleBackToDashboard() {
        try {
            System.out.println("DEBUG: Navigating back to Dashboard...");
            com.tourism.TourismApp.switchScene("/fxml/dashboard.fxml", "Dashboard");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Navigated back to Dashboard from Tourist Management");
        } catch (Exception e) {
            System.err.println("Error navigating back to Dashboard: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error navigating back: " + e.getMessage(), false);
        }
    }

    private void populateFormWithTourist(Tourist tourist) {
        if (tourist != null) {
            fullNameField.setText(tourist.getFullName());
            emailField.setText(tourist.getEmail());
            phoneNumberField.setText(tourist.getPhoneNumber());
            nationalityField.setText(tourist.getNationality());
            activeCheckBox.setSelected(tourist.isActive());
        }
    }

    private void clearForm() {
        if (fullNameField != null) fullNameField.clear();
        if (emailField != null) emailField.clear();
        if (phoneNumberField != null) phoneNumberField.clear();
        if (nationalityField != null) nationalityField.clear();
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
