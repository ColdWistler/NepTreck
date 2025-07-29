package com.tourism.controllers;

import com.tourism.models.FestivalDiscount;
import com.tourism.services.FestivalDiscountService;
import com.tourism.utils.FileDataManager;
import com.tourism.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FestivalOfferController implements Initializable {

    // FXML elements from festival-offers-management-view.fxml
    @FXML private TextField offerNameField;
    @FXML private TextField discountPercentageField;
    @FXML private CheckBox activeCheckBox;
    @FXML private Button createOfferButton;
    @FXML private Button updateOfferButton;
    @FXML private Button deleteOfferButton;
    @FXML private Button clearFormButton;
    @FXML private Label statusLabel;
    @FXML private TableView<FestivalDiscount> offersTable;
    @FXML private TableColumn<FestivalDiscount, String> offerIdColumn;
    @FXML private TableColumn<FestivalDiscount, String> offerNameColumn;
    @FXML private TableColumn<FestivalDiscount, Double> discountPercentageColumn;
    @FXML private TableColumn<FestivalDiscount, Boolean> activeColumn;
    @FXML private Button refreshTableButton;
    @FXML private Button backButton; // Back button

    private FestivalDiscountService festivalDiscountService;
    private ObservableList<FestivalDiscount> offersList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DEBUG: FestivalOfferController initializing...");
        festivalDiscountService = new FestivalDiscountService();

        setupTable();
        loadOffers();
        setupEventHandlers();

        clearStatusMessage();
        System.out.println("DEBUG: FestivalOfferController initialized.");
    }

    private void setupTable() {
        System.out.println("DEBUG: Setting up Offers table columns...");
        if (offerIdColumn != null) {
            offerIdColumn.setCellValueFactory(new PropertyValueFactory<>("discountId"));
        }
        if (offerNameColumn != null) {
            offerNameColumn.setCellValueFactory(new PropertyValueFactory<>("festivalName"));
        }
        if (discountPercentageColumn != null) {
            discountPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));
        }
        if (activeColumn != null) {
            activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        }

        offersList = FXCollections.observableArrayList();
        if (offersTable != null) {
            offersTable.setItems(offersList);
            System.out.println("DEBUG: Offers table items set to ObservableList.");
        } else {
            System.err.println("ERROR: offersTable is null during setupTable! Check FXML fx:id.");
        }
        System.out.println("DEBUG: Festival Offers Table setup complete.");
    }

    private void setupEventHandlers() {
        System.out.println("DEBUG: Setting up Festival Offers event handlers...");
        if (createOfferButton != null) {
            createOfferButton.setOnAction(event -> handleCreateOffer());
        }
        if (updateOfferButton != null) {
            updateOfferButton.setOnAction(event -> handleUpdateOffer());
        }
        if (deleteOfferButton != null) {
            deleteOfferButton.setOnAction(event -> handleDeleteOffer());
        }
        if (clearFormButton != null) {
            clearFormButton.setOnAction(event -> clearForm());
        }
        if (refreshTableButton != null) {
            refreshTableButton.setOnAction(event -> handleRefreshTable());
        }
        if (backButton != null) {
            backButton.setOnAction(event -> handleBackToDashboard());
        }

        // Add listener for table row selection to populate the form
        if (offersTable != null) {
            offersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateFormWithOffer(newValue);
                    }
                }
            );
        }
        System.out.println("DEBUG: Festival Offers Event handlers setup complete.");
    }

    private void loadOffers() {
        try {
            System.out.println("DEBUG: Attempting to load offers into table...");
            List<FestivalDiscount> offers = festivalDiscountService.getAllDiscounts();
            System.out.println("DEBUG: Offers retrieved from service: " + (offers != null ? offers.size() : "null") + " items.");

            if (offersList != null) {
                offersList.clear();
                if (offers != null) {
                    offersList.addAll(offers);
                }
                System.out.println("DEBUG: Offers ObservableList updated. Current size: " + offersList.size());
            } else {
                System.err.println("ERROR: offersList is null! Cannot update offers table.");
            }

        } catch (Exception e) {
            System.err.println("Error loading offers for table: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error loading offers: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleCreateOffer() {
        System.out.println("DEBUG: handleCreateOffer called.");
        try {
            if (offerNameField.getText().isEmpty() || discountPercentageField.getText().isEmpty()) {
                showStatus("Offer Name and Discount Percentage are required.", false);
                return;
            }

            Double discountPercentage;
            try {
                discountPercentage = Double.parseDouble(discountPercentageField.getText().trim());
            } catch (NumberFormatException e) {
                showStatus("Discount Percentage must be a valid number.", false);
                return;
            }
            if (discountPercentage < 0 || discountPercentage > 100) {
                showStatus("Discount Percentage must be between 0 and 100.", false);
                return;
            }

            FestivalDiscount newOffer = new FestivalDiscount();
            newOffer.setDiscountId("DISC" + System.currentTimeMillis()); // Generate ID
            newOffer.setFestivalName(offerNameField.getText().trim());
            newOffer.setDiscountPercentage(discountPercentage);
            newOffer.setActive(activeCheckBox.isSelected());

            boolean success = festivalDiscountService.saveDiscount(newOffer);

            if (success) {
                showStatus("Offer added successfully!", true);
                clearForm();
                loadOffers();
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Created festival offer: " + newOffer.getFestivalName());
            } else {
                showStatus("Failed to add offer.", false);
            }
        } catch (Exception e) {
            System.err.println("Error creating offer: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error creating offer: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleUpdateOffer() {
        System.out.println("DEBUG: handleUpdateOffer called.");
        FestivalDiscount selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showStatus("Please select an offer to update.", false);
            return;
        }

        try {
            if (offerNameField.getText().isEmpty() || discountPercentageField.getText().isEmpty()) {
                showStatus("Offer Name and Discount Percentage are required.", false);
                return;
            }

            Double discountPercentage;
            try {
                discountPercentage = Double.parseDouble(discountPercentageField.getText().trim());
            } catch (NumberFormatException e) {
                showStatus("Discount Percentage must be a valid number.", false);
                return;
            }
            if (discountPercentage < 0 || discountPercentage > 100) {
                showStatus("Discount Percentage must be between 0 and 100.", false);
                return;
            }

            selectedOffer.setFestivalName(offerNameField.getText().trim());
            selectedOffer.setDiscountPercentage(discountPercentage);
            selectedOffer.setActive(activeCheckBox.isSelected());

            boolean success = festivalDiscountService.saveDiscount(selectedOffer);

            if (success) {
                showStatus("Offer updated successfully!", true);
                clearForm();
                loadOffers();
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Updated festival offer: " + selectedOffer.getFestivalName());
            } else {
                showStatus("Failed to update offer.", false);
            }
        } catch (Exception e) {
            System.err.println("Error updating offer: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error updating offer: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleDeleteOffer() {
        System.out.println("DEBUG: handleDeleteOffer called.");
        FestivalDiscount selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showStatus("Please select an offer to delete.", false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Festival Offer");
        alert.setContentText("Are you sure you want to delete this offer: " + selectedOffer.getFestivalName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = festivalDiscountService.deleteDiscount(selectedOffer.getDiscountId());
                if (success) {
                    showStatus("Offer deleted successfully!", true);
                    clearForm();
                    loadOffers();
                    FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Deleted festival offer: " + selectedOffer.getFestivalName());
                } else {
                    showStatus("Failed to delete offer.", false);
                }
            } catch (Exception e) {
                System.err.println("Error deleting offer: " + e.getMessage());
                e.printStackTrace();
                showStatus("Error deleting offer: " + e.getMessage(), false);
            }
        }
    }

    @FXML
    private void handleClearForm() {
        System.out.println("DEBUG: handleClearForm called.");
        clearForm();
        offersTable.getSelectionModel().clearSelection();
        clearStatusMessage();
    }

    @FXML
    private void handleRefreshTable() {
        System.out.println("DEBUG: handleRefreshTable called.");
        loadOffers();
        showStatus("Offers data refreshed.", true);
    }

    // Method to handle returning to the Dashboard
    @FXML
    private void handleBackToDashboard() {
        try {
            System.out.println("DEBUG: Navigating back to Dashboard...");
            com.tourism.TourismApp.switchScene("/fxml/dashboard.fxml", "Dashboard");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Navigated back to Dashboard from Festival Offers Management");
        } catch (Exception e) {
            System.err.println("Error navigating back to Dashboard: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error navigating back: " + e.getMessage(), false);
        }
    }

    private void populateFormWithOffer(FestivalDiscount offer) {
        if (offer != null) {
            offerNameField.setText(offer.getFestivalName());
            discountPercentageField.setText(String.valueOf(offer.getDiscountPercentage()));
            activeCheckBox.setSelected(offer.isActive());
        }
    }

    private void clearForm() {
        if (offerNameField != null) offerNameField.clear();
        if (discountPercentageField != null) discountPercentageField.clear();
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
