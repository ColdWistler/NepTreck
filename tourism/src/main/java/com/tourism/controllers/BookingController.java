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
    @FXML private Button refreshButton;

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> packageColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Double> totalAmountColumn;
    @FXML private TableColumn<Booking, LocalDateTime> travelDateColumn;

    @FXML private Label statusLabel;

    private BookingService bookingService;
    private TouristService touristService;
    private TourPackageService packageService;
    private GuideService guideService;
    private ObservableList<Booking> bookingsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        bookingService = new BookingService();
        touristService = new TouristService();
        packageService = new TourPackageService();
        guideService = new GuideService();

        // Initialize table
        setupTable();

        // Load data
        loadComboBoxData();
        loadBookings();

        // Setup event handlers
        setupEventHandlers();

        clearStatusMessage();
    }

    private void setupTable() {
        if (bookingIdColumn != null) {
            bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        }
        if (touristColumn != null) {
            touristColumn.setCellValueFactory(new PropertyValueFactory<>("touristId"));
        }
        if (packageColumn != null) {
            packageColumn.setCellValueFactory(new PropertyValueFactory<>("packageId"));
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
        }

    }

    private void setupEventHandlers() {
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

        // Table selection handler
        if (bookingsTable != null) {
            bookingsTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            populateFormWithBooking(newValue);
                        }
                    }
            );
        }
    }

    private void loadComboBoxData() {
        try {
            // Load tourists
            if (touristComboBox != null) {
                List<Tourist> tourists = touristService.getAllTourists();
                touristComboBox.setItems(FXCollections.observableArrayList(tourists));
            }

            // Load packages
            if (packageComboBox != null) {
                List<TourPackage> packages = packageService.getAllPackages();
                packageComboBox.setItems(FXCollections.observableArrayList(packages));
            }

            // Load guides
            if (guideComboBox != null) {
                List<Guide> guides = guideService.getAllGuides();
                guideComboBox.setItems(FXCollections.observableArrayList(guides));
            }

        } catch (Exception e) {
            System.err.println("Error loading combo box data: " + e.getMessage());
            showStatus("Error loading data: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleCreateBooking() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Get form data
            Tourist selectedTourist = touristComboBox.getValue();
            TourPackage selectedPackage = packageComboBox.getValue();
            Guide selectedGuide = guideComboBox.getValue();
            LocalDate travelDate = travelDatePicker.getValue();
            int numberOfPeople = Integer.parseInt(numberOfPeopleField.getText().trim());
            String specialRequests = specialRequestsArea.getText().trim();

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
                loadBookings();
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                        "Created booking for tourist: " + selectedTourist.getFullName());
            } else {
                showStatus("Failed to create booking. Please try again.", false);
            }

        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            showStatus("Error creating booking: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleUpdateBooking() {
        try {
            Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
            if (selectedBooking == null) {
                showStatus("Please select a booking to update.", false);
                return;
            }

            // For now, just update the status to CONFIRMED
            boolean success = bookingService.confirmBooking(selectedBooking.getBookingId());

            if (success) {
                showStatus("Booking updated successfully!", true);
                loadBookings();
                FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                        "Updated booking: " + selectedBooking.getBookingId());
            } else {
                showStatus("Failed to update booking.", false);
            }

        } catch (Exception e) {
            System.err.println("Error updating booking: " + e.getMessage());
            showStatus("Error updating booking: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleDeleteBooking() {
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
                boolean success = FileDataManager.deleteBooking(selectedBooking.getBookingId());

                if (success) {
                    showStatus("Booking deleted successfully!", true);
                    loadBookings();
                    clearForm();
                    FileDataManager.logActivity(SessionManager.getCurrentUser().getUsername(),
                            "Deleted booking: " + selectedBooking.getBookingId());
                } else {
                    showStatus("Failed to delete booking.", false);
                }
            }

        } catch (Exception e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            showStatus("Error deleting booking: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleRefresh() {
        loadBookings();
        loadComboBoxData();
        showStatus("Data refreshed.", true);
    }

    private void loadBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            bookingsList.clear();
            bookingsList.addAll(bookings);

        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
            showStatus("Error loading bookings: " + e.getMessage(), false);
        }
    }

    private boolean validateInput() {
        if (touristComboBox.getValue() == null) {
            showStatus("Please select a tourist.", false);
            return false;
        }
        if (packageComboBox.getValue() == null) {
            showStatus("Please select a tour package.", false);
            return false;
        }
        if (travelDatePicker.getValue() == null) {
            showStatus("Please select a travel date.", false);
            return false;
        }
        if (numberOfPeopleField.getText().trim().isEmpty()) {
            showStatus("Please enter number of people.", false);
            return false;
        }

        try {
            int numberOfPeople = Integer.parseInt(numberOfPeopleField.getText().trim());
            if (numberOfPeople <= 0) {
                showStatus("Number of people must be greater than 0.", false);
                return false;
            }
        } catch (NumberFormatException e) {
            showStatus("Please enter a valid number for people count.", false);
            return false;
        }

        return true;
    }

    private void populateFormWithBooking(Booking booking) {
        try {
            // Find and select tourist
            Tourist tourist = touristService.getTouristById(booking.getTouristId());
            if (tourist != null) {
                touristComboBox.setValue(tourist);
            }

            // Find and select package
            TourPackage tourPackage = packageService.getPackageById(booking.getPackageId());
            if (tourPackage != null) {
                packageComboBox.setValue(tourPackage);
            }

            // Find and select guide
            if (booking.getGuideId() != null) {
                Guide guide = guideService.getGuideById(booking.getGuideId());
                if (guide != null) {
                    guideComboBox.setValue(guide);
                }
            }

            // Set travel date
            if (booking.getTravelDate() != null) {
                travelDatePicker.setValue(booking.getTravelDate().toLocalDate());
            }

            // Set number of people
            if (booking.getNumberOfPeople() != null) {
                numberOfPeopleField.setText(booking.getNumberOfPeople().toString());
            }

            // Set special requests
            if (booking.getSpecialRequests() != null) {
                specialRequestsArea.setText(booking.getSpecialRequests());
            }

        } catch (Exception e) {
            System.err.println("Error populating form: " + e.getMessage());
        }
    }

    private void clearForm() {
        if (touristComboBox != null) touristComboBox.setValue(null);
        if (packageComboBox != null) packageComboBox.setValue(null);
        if (guideComboBox != null) guideComboBox.setValue(null);
        if (travelDatePicker != null) travelDatePicker.setValue(null);
        if (numberOfPeopleField != null) numberOfPeopleField.clear();
        if (specialRequestsArea != null) specialRequestsArea.clear();
    }

    private void showStatus(String message, boolean isSuccess) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isSuccess ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        }
    }

    private void clearStatusMessage() {
        if (statusLabel != null) {
            statusLabel.setText("");
        }
    }
}
