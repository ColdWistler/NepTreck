package com.tourism.services;

import com.tourism.models.Booking;
import com.tourism.models.Tourist;
import com.tourism.models.TourPackage;
import com.tourism.utils.FileDataManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService {

    public boolean createBooking(String touristId, String packageId, String guideId,
                                 LocalDateTime travelDate, int numberOfPeople, String specialRequests) {
        try {
            // Generate unique booking ID
            String bookingId = generateBookingId();

            // Create new booking
            Booking booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setTouristId(touristId);
            booking.setPackageId(packageId);
            booking.setGuideId(guideId);
            booking.setTravelDate(travelDate);
            booking.setNumberOfPeople(numberOfPeople);
            booking.setSpecialRequests(specialRequests);
            booking.setStatus("PENDING");
            booking.setPaymentStatus("PENDING");
            booking.setCreatedAt(LocalDateTime.now());

            // Calculate total amount (you can implement pricing logic here)
            double totalAmount = calculateTotalAmount(packageId, numberOfPeople);
            booking.setTotalAmount(totalAmount);

            // Save booking
            boolean saved = FileDataManager.saveBooking(booking);

            if (saved) {
                FileDataManager.logActivity("SYSTEM", "Booking created: " + bookingId);
            }

            return saved;

        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            FileDataManager.logActivity("SYSTEM", "Booking creation error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBookingStatus(String bookingId, String status) {
        try {
            Booking booking = FileDataManager.findBookingById(bookingId);
            if (booking != null) {
                booking.setStatus(status);
                booking.setUpdatedAt(LocalDateTime.now());

                boolean updated = FileDataManager.saveBooking(booking);

                if (updated) {
                    FileDataManager.logActivity("SYSTEM", "Booking status updated: " + bookingId + " -> " + status);
                }

                return updated;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelBooking(String bookingId) {
        return updateBookingStatus(bookingId, "CANCELLED");
    }

    public boolean confirmBooking(String bookingId) {
        return updateBookingStatus(bookingId, "CONFIRMED");
    }

    public List<Booking> getBookingsByTourist(String touristId) {
        try {
            List<Booking> allBookings = FileDataManager.getAllBookings();
            return allBookings.stream()
                    .filter(booking -> touristId.equals(booking.getTouristId()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error getting bookings by tourist: " + e.getMessage());
            return List.of();
        }
    }

    public List<Booking> getBookingsByStatus(String status) {
        try {
            List<Booking> allBookings = FileDataManager.getAllBookings();
            return allBookings.stream()
                    .filter(booking -> status.equals(booking.getStatus()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error getting bookings by status: " + e.getMessage());
            return List.of();
        }
    }

    public List<Booking> getAllBookings() {
        try {
            return FileDataManager.getAllBookings();
        } catch (Exception e) {
            System.err.println("Error getting all bookings: " + e.getMessage());
            return List.of();
        }
    }

    public Booking getBookingById(String bookingId) {
        try {
            return FileDataManager.findBookingById(bookingId);
        } catch (Exception e) {
            System.err.println("Error getting booking by ID: " + e.getMessage());
            return null;
        }
    }

    public boolean updatePaymentStatus(String bookingId, String paymentStatus) {
        try {
            Booking booking = FileDataManager.findBookingById(bookingId);
            if (booking != null) {
                booking.setPaymentStatus(paymentStatus);
                booking.setUpdatedAt(LocalDateTime.now());

                boolean updated = FileDataManager.saveBooking(booking);

                if (updated) {
                    FileDataManager.logActivity("SYSTEM", "Payment status updated: " + bookingId + " -> " + paymentStatus);
                }

                return updated;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    private String generateBookingId() {
        return "BK" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private double calculateTotalAmount(String packageId, int numberOfPeople) {
        try {
            TourPackage tourPackage = FileDataManager.findTourPackageById(packageId);
            if (tourPackage != null && tourPackage.getPrice() != null) {
                return tourPackage.getPrice() * numberOfPeople;
            }
            return 0.0; // Default price if package not found

        } catch (Exception e) {
            System.err.println("Error calculating total amount: " + e.getMessage());
            return 0.0;
        }
    }

    public int getTotalBookingsCount() {
        try {
            return FileDataManager.getAllBookings().size();
        } catch (Exception e) {
            System.err.println("Error getting total bookings count: " + e.getMessage());
            return 0;
        }
    }

    public int getPendingBookingsCount() {
        try {
            return getBookingsByStatus("PENDING").size();
        } catch (Exception e) {
            System.err.println("Error getting pending bookings count: " + e.getMessage());
            return 0;
        }
    }

    public int getConfirmedBookingsCount() {
        try {
            return getBookingsByStatus("CONFIRMED").size();
        } catch (Exception e) {
            System.err.println("Error getting confirmed bookings count: " + e.getMessage());
            return 0;
        }
    }
}