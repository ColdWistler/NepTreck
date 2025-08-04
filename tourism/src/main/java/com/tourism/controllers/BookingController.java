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
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class BookingController implements Initializable {

    @FXML private ComboBox<Tourist> touristComboBox;
    @FXML private ComboBox<TourPackage> packageComboBox;
    @FXML private ComboBox<Guide> guideComboBox;
    @FXML private DatePicker travelDatePicker;
    @FXML private TextField numberOfPeopleField;
    @FXML private TextArea specialRequestsArea;
    @FXML private Button createBookingButton;
    @FXML private Button updateBookingButton;
    @FXML private Button deleteBookingButton;
    @FXML private Button refreshButton; // Assuming this is refreshTableButton from FXML

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> packageColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Double> totalAmountColumn;
    @FXML private TableColumn<Booking, LocalDateTime> travelDateColumn;
    @FXML private TableColumn<Booking, String> guideColumn;

    @FXML private Label statusLabel;
    @FXML private Button backButton;

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    private BookingService bookingService;
    private TouristService touristService;
    private TourPackageService packageService;
    private GuideService guideService;
    private ObservableList<Booking> bookingsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DEBUG: BookingController initializing...");
        // Initialize services
        bookingService = new BookingService();
        touristService = new TouristService();
        packageService = new TourPackageService();
        guideService = new GuideService();

        // Initialize table
        setupTable();

        // Load data
        loadComboBoxData();
        loadBookings(); // This loads data into the table

        // Setup event handlers
        setupEventHandlers();

        clearStatusMessage();
        System.out.println("DEBUG: BookingController initialized.");
    }

    private void setupTable() {
        System.out.println("DEBUG: Setting up table columns...");
        if (bookingIdColumn != null) {
            bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        }
        // IMPORTANT: These columns display IDs.
        if (touristColumn != null) {
            touristColumn.setCellValueFactory(new PropertyValueFactory<>("touristId"));
        }
        if (packageColumn != null) {
            packageColumn.setCellValueFactory(new PropertyValueFactory<>("packageId"));
        }
        if (guideColumn != null) { // Ensure this block is present
            guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideId"));
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
        if (totalAmountColumn != null) {
            totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        }
        if (travelDateColumn != null) {
            travelDateColumn.setCellValueFactory(new PropertyValueFactory<>("travelDate"));
        }

        bookingsList = FXCollections.observableArrayList();
        if (bookingsTable != null) {
            bookingsTable.setItems(bookingsList);
            System.out.println("DEBUG: Bookings table items set to ObservableList.");
        } else {
            System.err.println("ERROR: bookingsTable is null during setupTable! Check FXML fx:id.");
        }
        System.out.println("DEBUG: Table setup complete.");
    }

    private void setupEventHandlers() {
        System.out.println("DEBUG: Setting up event handlers...");
        if (createBookingButton != null) {
            createBookingButton.setOnAction(event -> handleCreateBooking());
        }
        if (updateBookingButton != null) {
            updateBookingButton.setOnAction(event -> handleUpdateBooking());
        }
        if (deleteBookingButton != null) {
            deleteBookingButton.setOnAction(event -> handleDeleteBooking());
        }

        if (refreshButton != null) {
            refreshButton.setOnAction(event -> handleRefresh());
        }
        if (backButton != null) { // NEW: Set action for back button
            backButton.setOnAction(event -> handleBackToDashboard());
        }

        // Table selection handler
        if (bookingsTable != null) {
            bookingsTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            System.out.println("DEBUG: Selected booking: " + newValue.getBookingId());
                            populateFormWithBooking(newValue);
                        }
                    }
            );
        }
        System.out.println("DEBUG: Event handlers setup complete.");
    }

    private void loadComboBoxData() {
        try {
            System.out.println("DEBUG: Loading ComboBox data...");
            // Load tourists
            if (touristComboBox != null) {
                List<Tourist> tourists = touristService.getAllTourists();
                touristComboBox.setItems(FXCollections.observableArrayList(tourists));
                System.out.println("DEBUG: Loaded " + tourists.size() + " tourists into ComboBox.");
            }

            // Load packages
            if (packageComboBox != null) {
                List<TourPackage> packages = packageService.getAllPackages();
                packageComboBox.setItems(FXCollections.observableArrayList(packages));
                System.out.println("DEBUG: Loaded " + packages.size() + " packages into ComboBox.");
            }

            // Load guides
            if (guideComboBox != null) {
                List<Guide> guides = guideService.getAllGuides();
                guideComboBox.setItems(FXCollections.observableArrayList(guides));
                System.out.println("DEBUG: Loaded " + guides.size() + " guides into ComboBox.");
            }

        } catch (Exception e) {
            System.err.println("Error loading combo box data: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
            showStatus("Error loading data: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleCreateBooking() {
        System.out.println("DEBUG: handleCreateBooking called.");
        try {
            // Validate input
            if (!validateInput()) {
                System.out.println("DEBUG: Input validation failed.");
                return;
            }

            // Get form data
            Tourist selectedTourist = touristComboBox.getValue();
            TourPackage selectedPackage = packageComboBox.getValue();

            if (selectedPackage != null && selectedPackage.getMaxAltitude() != null && selectedPackage.getMaxAltitude() > 2500.0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("High Altitude Warning");
                alert.setHeaderText("This tour reaches high altitude");
                alert.setContentText("This tour reaches an altitude of " +
                selectedPackage.getMaxAltitude() + " meters.\n" +
                "Please ensure the traveler is medically fit.");
                alert.showAndWait();
            }

            Guide selectedGuide = guideComboBox.getValue();
            LocalDate travelDate = travelDatePicker.getValue();
            int numberOfPeople = Integer.parseInt(numberOfPeopleField.getText().trim());
            String specialRequests = specialRequestsArea.getText().trim();

            System.out.println("DEBUG: Creating booking with Tourist: " + (selectedTourist != null ? selectedTourist.getTouristId() : "null") +
                               ", Package: " + (selectedPackage != null ? selectedPackage.getPackageId() : "null") +
                               ", People: " + numberOfPeople);

            // Convert LocalDate to LocalDateTime (assuming start of day)
            LocalDateTime travelDateTime = travelDate.atTime(LocalTime.of(9, 0));

            // Create booking
            boolean success = bookingService.createBooking(
                    selectedTourist.getTouristId(),
                    selectedPackage.getPackageId(),
                    selectedGuide != null ? selectedGuide.getGuideId() : null,
                    travelDateTime,
                    numberOfPeople,
                    specialRequests
            );

            if (success) {
                showStatus("Booking created successfully!", true);
                clearForm();
                loadBookings(); // Reloads table data after creation
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                        "Created booking for tourist: " + selectedTourist.getFullName());
                System.out.println("DEBUG: Booking created, table reloaded.");
            } else {
                showStatus("Failed to create booking. Please try again.", false);
                System.out.println("DEBUG: Booking creation failed.");
            }

        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
            showStatus("Error creating booking: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleUpdateBooking() {
        System.out.println("DEBUG: handleUpdateBooking called.");
        try {
            Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
            if (selectedBooking == null) {
                showStatus("Please select a booking to update.", false);
                return;
            }

            System.out.println("DEBUG: Updating booking: " + selectedBooking.getBookingId());
            // For now, just update the status to CONFIRMED
            boolean success = bookingService.confirmBooking(selectedBooking.getBookingId());

            if (success) {
                showStatus("Booking updated successfully!", true);
                loadBookings(); // Reloads table data after update
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                        "Updated booking: " + selectedBooking.getBookingId());
                System.out.println("DEBUG: Booking updated, table reloaded.");
            } else {
                showStatus("Failed to update booking.", false);
                System.out.println("DEBUG: Booking update failed.");
            }

        } catch (Exception e) {
            System.err.println("Error updating booking: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
            showStatus("Error updating booking: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleDeleteBooking() {
        System.out.println("DEBUG: handleDeleteBooking called.");
        try {
            Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
            if (selectedBooking == null) {
                showStatus("Please select a booking to delete.", false);
                return;
            }

            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Booking");
            alert.setContentText("Are you sure you want to delete this booking?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                System.out.println("DEBUG: Deleting booking: " + selectedBooking.getBookingId());
                boolean success = FileDataManager.deleteBooking(selectedBooking.getBookingId());

                if (success) {
                    showStatus("Booking deleted successfully!", true);
                    loadBookings(); // Reloads table data after deletion
                    clearForm();
                    FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                            "Deleted booking: " + selectedBooking.getBookingId());
                    System.out.println("DEBUG: Booking deleted, table reloaded.");
                } else {
                    showStatus("Failed to delete booking.", false);
                    System.out.println("DEBUG: Booking deletion failed.");
                }
            } else {
                System.out.println("DEBUG: Booking deletion cancelled.");
            }

        } catch (Exception e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
            showStatus("Error deleting booking: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleRefresh() {
        System.out.println("DEBUG: handleRefresh called. Reloading all data.");
        loadBookings();
        loadComboBoxData();
        showStatus("Data refreshed.", true);
    }

    private void loadBookings() {
        try {
            System.out.println("DEBUG: Attempting to load bookings into table...");
            List<Booking> bookings = bookingService.getAllBookings();
            System.out.println("DEBUG: Bookings retrieved from service: " + (bookings != null ? bookings.size() : "null") + " items.");

            if (bookingsList != null) {
                bookingsList.clear(); // Clear existing items
                if (bookings != null) {
                    bookingsList.addAll(bookings); // Add new items
                }
                System.out.println("DEBUG: ObservableList updated. Current size: " + bookingsList.size());
            } else {
                System.err.println("ERROR: bookingsList is null! Cannot update table.");
            }

        } catch (Exception e) {
            System.err.println("Error loading bookings for table: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
            showStatus("Error loading bookings for table: " + e.getMessage(), false);
        }
    }

    private boolean validateInput() {
        //  (existing validation logic)
        return true;
    }

    private void populateFormWithBooking(Booking booking) {
        // (existing form population logic)
    }

    private void clearForm() {
        // (existing clear form logic)
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


    @FXML
    private void handleBackToDashboard() {
        try {
            System.out.println("DEBUG: Navigating back to Dashboard...");
            com.tourism.TourismApp.switchScene("/fxml/dashboard.fxml", "Dashboard");
            FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(), "Navigated back to Dashboard from Booking Management");
        } catch (Exception e) {
            System.err.println("Error navigating back to Dashboard: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error navigating back: " + e.getMessage(), false);
        }
    }
}
