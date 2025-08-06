package com.tourism.controllers;

import com.tourism.models.Booking;
import com.tourism.models.Tourist;
import com.tourism.models.TourPackage;
import com.tourism.models.Guide;
import com.tourism.services.BookingService;
import com.tourism.services.TouristService;
import com.tourism.services.TourPackageService;
import com.tourism.services.GuideService;
import com.tourism.utils.FileDataManager;
import com.tourism.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID; // For generating unique IDs

public class StaffBookingController implements Initializable {

    // FXML elements from booking-form.fxml
    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private ComboBox<String> countryComboBox; // Assuming country is a String
    @FXML private TextArea addressArea;
    @FXML private TextField numberOfPeopleField; // Added this field

    @FXML private RadioButton normalPackageRadio;
    @FXML private RadioButton premiumPackageRadio;
    @FXML private ToggleGroup packageTypeToggleGroup; // To group radio buttons

    @FXML private ComboBox<TourPackage> attractionsComboBox; // Renamed from packageComboBox for clarity in user form
    @FXML private ComboBox<String> transportationComboBox; // Assuming transportation is a String

    @FXML private CheckBox relaxingCheckBox;
    @FXML private CheckBox historicCheckBox;
    @FXML private CheckBox museumsCheckBox;
    @FXML private CheckBox trekkingCheckBox;

    @FXML private TextField packageNameField; // From Timeline section, promptText="Package Name"
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private Label totalAmountLabel;
    @FXML private Button confirmButton;
    @FXML private Button submitButton;

    // Services
    private BookingService bookingService;
    private TouristService touristService;
    private TourPackageService packageService;
    private GuideService guideService; // Though not directly used in this form, good to have for consistency

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DEBUG: StaffBookingController initializing...");
        // Initialize services
        bookingService = new BookingService();
        touristService = new TouristService();
        packageService = new TourPackageService();
        guideService = new GuideService(); // Initialize guide service

        // Setup toggle group for package types
        packageTypeToggleGroup = new ToggleGroup();
        normalPackageRadio.setToggleGroup(packageTypeToggleGroup);
        premiumPackageRadio.setToggleGroup(packageTypeToggleGroup);

        // Load data for combo boxes
        loadComboBoxData();

        // Setup event handlers
        setupEventHandlers();

        // Initial state
        clearForm();
        updateTotalAmount(); // Calculate initial total
        System.out.println("DEBUG: StaffBookingController initialized.");
    }

    private void setupEventHandlers() {
        // Set action for submit button
        if (submitButton != null) {
            submitButton.setOnAction(event -> handleSubmitBooking());
        }
        // Set action for confirm button
        if (confirmButton != null) {
            confirmButton.setOnAction(event -> handleConfirmBooking());
        }

        // Listen for changes in package selection to update total amount
        if (normalPackageRadio != null) {
            normalPackageRadio.selectedProperty().addListener((obs, oldVal, newVal) -> updateTotalAmount());
        }
        if (premiumPackageRadio != null) {
            premiumPackageRadio.selectedProperty().addListener((obs, oldVal, newVal) -> updateTotalAmount());
        }
        if (attractionsComboBox != null) {
            attractionsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalAmount());
        }
        if (fromDatePicker != null) {
            fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalAmount());
        }
        if (toDatePicker != null) {
            toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalAmount());
        }
        // Enable submit button only if required fields are filled (basic example)
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        contactField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        countryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        attractionsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        numberOfPeopleField.textProperty().addListener((obs, oldVal, newVal) -> validateForm()); // Add listener for number of people
    }

    private void loadComboBoxData() {
        try {
            // Load countries (example data, you might have a service for this)
            ObservableList<String> countries = FXCollections.observableArrayList(
                    "Nepal", "India", "China", "USA", "UK", "Australia", "Canada"
            );
            if (countryComboBox != null) {
                countryComboBox.setItems(countries);
            }

            // Load tour packages into attractionsComboBox
            if (attractionsComboBox != null) {
                List<TourPackage> packages = packageService.getAllPackages();
                attractionsComboBox.setItems(FXCollections.observableArrayList(packages));
                // Set a cell factory to display package name instead of object reference
                attractionsComboBox.setCellFactory(lv -> new ListCell<TourPackage>() {
                    @Override
                    protected void updateItem(TourPackage item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getPackageName());
                    }
                });
                attractionsComboBox.setButtonCell(new ListCell<TourPackage>() {
                    @Override
                    protected void updateItem(TourPackage item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getPackageName());
                    }
                });
            }

            // Load transportation preferences (example data)
            ObservableList<String> transportationOptions = FXCollections.observableArrayList(
                    "Flight", "Bus", "Private Car", "Train"
            );
            if (transportationComboBox != null) {
                transportationComboBox.setItems(transportationOptions);
            }

        } catch (Exception e) {
            System.err.println("Error loading combo box data: " + e.getMessage());
            showAlert("Error loading data: " + e.getMessage());
        }
    }

    private void updateTotalAmount() {
        double total = 0.0;

        // Package type
        if (normalPackageRadio.isSelected()) {
            total += 1000.0; // Example price for normal package
        } else if (premiumPackageRadio.isSelected()) {
            total += 2000.0; // Example price for premium package
        }

        // Selected attraction/package
        TourPackage selectedPackage = attractionsComboBox.getValue();
        if (selectedPackage != null) {
            total += selectedPackage.getPrice(); // Assuming TourPackage has a getPrice() method
        }

        // Calculate duration for timeline cost
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        if (fromDate != null && toDate != null && !toDate.isBefore(fromDate)) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(fromDate, toDate) + 1;
            total += days * 50.0; // Example daily rate
        }

        // Add cost based on number of people
        try {
            int numberOfPeople = Integer.parseInt(numberOfPeopleField.getText().trim());
            if (numberOfPeople > 0) {
                total += numberOfPeople * 100.0; // Example cost per person
            }
        } catch (NumberFormatException e) {
            // Ignore if input is not a valid number, total remains unchanged by this factor
        }


        totalAmountLabel.setText(String.format("$%.2f", total));
    }

    private void validateForm() {
        boolean isValid = !nameField.getText().trim().isEmpty() &&
                !contactField.getText().trim().isEmpty() &&
                countryComboBox.getValue() != null &&
                attractionsComboBox.getValue() != null &&
                fromDatePicker.getValue() != null &&
                toDatePicker.getValue() != null &&
                !toDatePicker.getValue().isBefore(fromDatePicker.getValue());

        // New validation for numberOfPeopleField
        try {
            int numPeople = Integer.parseInt(numberOfPeopleField.getText().trim());
            isValid = isValid && numPeople > 0;
        } catch (NumberFormatException e) {
            isValid = false; // Not a valid number
        }


        submitButton.setDisable(!isValid);
    }

    @FXML
    private void handleSubmitBooking() {
        System.out.println("DEBUG: handleSubmitBooking called.");
        try {
            if (!validateInput()) {
                showAlert("Please fill in all required fields and ensure dates and number of people are valid.");
                return;
            }

            // Create a new Tourist using the new 5-argument constructor
            String touristId = UUID.randomUUID().toString(); // Generate a unique ID for the tourist
            Tourist newTourist = new Tourist(
                    touristId,
                    nameField.getText().trim(),
                    contactField.getText().trim(), // Maps to phoneNumber
                    countryComboBox.getValue(),     // Maps to nationality
                    addressArea.getText().trim()    // Maps to address
            );

            TourPackage selectedPackage = attractionsComboBox.getValue();
            if (selectedPackage == null) {
                showAlert("Please select a tour package/attraction.");
                return;
            }

            // High altitude warning (similar to admin controller)
            if (selectedPackage.getMaxAltitude() != null && selectedPackage.getMaxAltitude() > 2500.0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("High Altitude Warning");
                alert.setHeaderText("This tour reaches high altitude");
                alert.setContentText("This tour reaches an altitude of " +
                        selectedPackage.getMaxAltitude() + " meters.\n" +
                        "Please ensure the traveler is medically fit.");
                alert.showAndWait();
            }

            // Collect activity preferences
            StringBuilder activities = new StringBuilder();
            if (relaxingCheckBox.isSelected()) activities.append("Relaxing;");
            if (historicCheckBox.isSelected()) activities.append("Historic Sites;");
            if (museumsCheckBox.isSelected()) activities.append("Museums;");
            if (trekkingCheckBox.isSelected()) activities.append("Trekking;");
            String specialRequests = activities.toString().isEmpty() ? "" : "Activities: " + activities.toString();
            if (!addressArea.getText().trim().isEmpty()) { // Using addressArea for additional requests
                specialRequests += (specialRequests.isEmpty() ? "" : " ") + "Additional Info: " + addressArea.getText().trim();
            }
            if (transportationComboBox.getValue() != null) {
                specialRequests += (specialRequests.isEmpty() ? "" : " ") + "Transportation: " + transportationComboBox.getValue();
            }
            if (!packageNameField.getText().trim().isEmpty()) {
                specialRequests += (specialRequests.isEmpty() ? "" : " ") + "Custom Package Name: " + packageNameField.getText().trim();
            }


            // Convert LocalDate to LocalDateTime (assuming start of day for travel date)
            LocalDateTime travelStartDateTime = fromDatePicker.getValue().atStartOfDay();

            // Get number of people from the new field
            int numberOfPeople = Integer.parseInt(numberOfPeopleField.getText().trim());

            // Create booking
            // The BookingService.createBooking method expects:
            // (String touristId, String packageId, String guideId, LocalDateTime travelDate, int numberOfPeople, String specialRequests)
            // It calculates total amount and sets status internally.
            boolean success = bookingService.createBooking(
                    newTourist.getTouristId(),
                    selectedPackage.getPackageId(),
                    null, // No guide selection on this form, pass null
                    travelStartDateTime,
                    numberOfPeople,
                    specialRequests
            );

            if (success) {
                showAlert("Booking submitted successfully! Status: Pending Confirmation.");
                clearForm();
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                        "Staff created new booking for: " + nameField.getText().trim());
            } else {
                showAlert("Failed to submit booking. Please try again.");
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid number format for people count.");
        } catch (Exception e) {
            System.err.println("Error submitting booking: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error submitting booking: " + e.getMessage());
        }
    }

    @FXML
    private void handleConfirmBooking() {

        // If the idea is to 'confirm' the details before submitting:
        if (validateInput()) {
            showAlert("Booking details confirmed. Click 'Submit' to finalize the booking.");
            submitButton.setDisable(false); // Enable submit after confirmation
        } else {
            showAlert("Please complete all required fields before confirming.");
        }
    }


    private boolean validateInput() {
        // Basic validation for required fields
        boolean baseValidation = !nameField.getText().trim().isEmpty() &&
                !contactField.getText().trim().isEmpty() &&
                countryComboBox.getValue() != null &&
                attractionsComboBox.getValue() != null &&
                fromDatePicker.getValue() != null &&
                toDatePicker.getValue() != null &&
                !toDatePicker.getValue().isBefore(fromDatePicker.getValue()) &&
                (normalPackageRadio.isSelected() || premiumPackageRadio.isSelected()); // Ensure a package type is selected

        // Validate number of people field
        try {
            int numPeople = Integer.parseInt(numberOfPeopleField.getText().trim());
            return baseValidation && numPeople > 0;
        } catch (NumberFormatException e) {
            return false; // Not a valid number for people count
        }
    }

    private void clearForm() {
        nameField.clear();
        contactField.clear();
        countryComboBox.getSelectionModel().clearSelection();
        addressArea.clear();
        numberOfPeopleField.clear(); // Clear number of people field
        normalPackageRadio.setSelected(false);
        premiumPackageRadio.setSelected(false);
        packageTypeToggleGroup.selectToggle(null); // Deselect all radio buttons
        attractionsComboBox.getSelectionModel().clearSelection();
        transportationComboBox.getSelectionModel().clearSelection();
        relaxingCheckBox.setSelected(false);
        historicCheckBox.setSelected(false);
        museumsCheckBox.setSelected(false);
        trekkingCheckBox.setSelected(false);
        packageNameField.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        totalAmountLabel.setText("$0.00");
        submitButton.setDisable(true); // Disable submit until valid input
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
