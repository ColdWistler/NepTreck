package com.tourism.utils;

import com.tourism.models.Booking;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Additional methods for FileDataManager to handle Booking operations
 * Add these methods to your existing FileDataManager.java class
 */
public class FileDataManagerBookingMethods {

    private static final String BOOKINGS_FILE = "data/bookings.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static boolean saveBooking(Booking booking) {
        try {
            List<Booking> bookings = getAllBookings();

            // Remove existing booking with same ID (for updates)
            bookings.removeIf(b -> booking.getBookingId().equals(b.getBookingId()));

            // Add the booking
            bookings.add(booking);

            // Save all bookings
            try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
                for (Booking b : bookings) {
                    writer.println(bookingToString(b));
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error saving booking: " + e.getMessage());
            return false;
        }
    }

    public static List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();

        try {
            File file = new File(BOOKINGS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return bookings;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Booking booking = stringToBooking(line);
                        if (booking != null) {
                            bookings.add(booking);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error reading bookings: " + e.getMessage());
        }

        return bookings;
    }

    public static Booking findBookingById(String bookingId) {
        List<Booking> bookings = getAllBookings();
        return bookings.stream()
                .filter(booking -> bookingId.equals(booking.getBookingId()))
                .findFirst()
                .orElse(null);
    }

    private static String bookingToString(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append(booking.getBookingId()).append("|");
        sb.append(booking.getTouristId()).append("|");
        sb.append(booking.getPackageId()).append("|");
        sb.append(booking.getGuideId() != null ? booking.getGuideId() : "").append("|");
        sb.append(booking.getBookingDate() != null ? booking.getBookingDate().format(DATE_FORMATTER) : "").append("|");
        sb.append(booking.getTravelDate() != null ? booking.getTravelDate().format(DATE_FORMATTER) : "").append("|");
        sb.append(booking.getStatus()).append("|");
        sb.append(booking.getTotalAmount() != null ? booking.getTotalAmount() : 0.0).append("|");
        sb.append(booking.getNumberOfPeople() != null ? booking.getNumberOfPeople() : 1).append("|");
        sb.append(booking.getSpecialRequests() != null ? booking.getSpecialRequests() : "").append("|");
        sb.append(booking.getPaymentStatus()).append("|");
        sb.append(booking.getCreatedAt() != null ? booking.getCreatedAt().format(DATE_FORMATTER) : "").append("|");
        sb.append(booking.getUpdatedAt() != null ? booking.getUpdatedAt().format(DATE_FORMATTER) : "");

        return sb.toString();
    }

    private static Booking stringToBooking(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                Booking booking = new Booking();
                booking.setBookingId(parts[0]);
                booking.setTouristId(parts[1]);
                booking.setPackageId(parts[2]);
                booking.setGuideId(parts[3].isEmpty() ? null : parts[3]);

                if (!parts[4].isEmpty()) {
                    booking.setBookingDate(LocalDateTime.parse(parts[4], DATE_FORMATTER));
                }
                if (!parts[5].isEmpty()) {
                    booking.setTravelDate(LocalDateTime.parse(parts[5], DATE_FORMATTER));
                }

                booking.setStatus(parts[6]);

                if (parts.length > 7 && !parts[7].isEmpty()) {
                    booking.setTotalAmount(Double.parseDouble(parts[7]));
                }
                if (parts.length > 8 && !parts[8].isEmpty()) {
                    booking.setNumberOfPeople(Integer.parseInt(parts[8]));
                }
                if (parts.length > 9) {
                    booking.setSpecialRequests(parts[9]);
                }
                if (parts.length > 10) {
                    booking.setPaymentStatus(parts[10]);
                }
                if (parts.length > 11 && !parts[11].isEmpty()) {
                    booking.setCreatedAt(LocalDateTime.parse(parts[11], DATE_FORMATTER));
                }
                if (parts.length > 12 && !parts[12].isEmpty()) {
                    booking.setUpdatedAt(LocalDateTime.parse(parts[12], DATE_FORMATTER));
                }

                return booking;
            }
        } catch (Exception e) {
            System.err.println("Error parsing booking line: " + line + " - " + e.getMessage());
        }

        return null;
    }
}