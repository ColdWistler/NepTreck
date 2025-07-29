package com.tourism.utils;

import com.tourism.models.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileDataManager {

    // File paths
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String TOURISTS_FILE = DATA_DIR + "tourists.txt";
    private static final String GUIDES_FILE = DATA_DIR + "guides.txt";
    private static final String PACKAGES_FILE = DATA_DIR + "packages.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.txt";
    private static final String DISCOUNTS_FILE = DATA_DIR + "discounts.txt";
    private static final String ACTIVITY_LOG_FILE = DATA_DIR + "activity.log";
    private static final String BACKUP_DIR = "backups/";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    static {
        initializeDataDirectory();
        initializeDefaultData();
    }

    private static void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(BACKUP_DIR));
        } catch (Exception e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    private static void initializeDefaultData() {
        try {
            // Create default users if file doesn't exist
            if (!Files.exists(Paths.get(USERS_FILE))) {
                List<User> defaultUsers = List.of(createDefaultAdmin(), createDefaultStaff());
                saveUsers(defaultUsers);
            }

            // Create default packages if file doesn't exist
            if (!Files.exists(Paths.get(PACKAGES_FILE))) {
                List<TourPackage> defaultPackages = createDefaultPackages();
                savePackages(defaultPackages);
            }

            // Create default discounts if file doesn't exist
            if (!Files.exists(Paths.get(DISCOUNTS_FILE))) {
                createDefaultDiscounts();
            }

        } catch (Exception e) {
            System.err.println("Error initializing default data: " + e.getMessage());
        }
    }

    private static User createDefaultAdmin() {
        User admin = new User("admin-001", "admin", "92668751", "ADMIN");
        admin.setFullName("System Administrator");
        admin.setAdminLevel("SUPER");
        admin.setActive(true);
        return admin;
    }

    private static User createDefaultStaff() {
        User staff = new User("staff-001", "staff", "19975738", "STAFF");
        staff.setFullName("Tourism Staff");
        staff.setActive(true);
        return staff;
    }

    private static List<TourPackage> createDefaultPackages() {
        List<TourPackage> packages = new ArrayList<>();

        // Package 1
        TourPackage pkg1 = new TourPackage("PKG001", "Kathmandu Valley Tour",
                "Explore the cultural heritage of Kathmandu", 150.0, 3, "Cultural");
        pkg1.setDestination("Kathmandu");
        pkg1.setDifficulty("EASY");
        pkg1.setSeason("ALL_YEAR");
        pkg1.setMaxParticipants(20);
        packages.add(pkg1);

        // Package 2
        TourPackage pkg2 = new TourPackage("PKG002", "Everest Base Camp Trek",
                "Adventure trek to Everest Base Camp", 1200.0, 14, "Adventure");
        pkg2.setDestination("Everest Region");
        pkg2.setDifficulty("HARD");
        pkg2.setSeason("SPRING");
        pkg2.setMaxParticipants(10);
        packages.add(pkg2);

        // Package 3
        TourPackage pkg3 = new TourPackage("PKG003", "Pokhara Lake Tour",
                "Scenic tour of Pokhara lakes and mountains", 200.0, 2, "Nature");
        pkg3.setDestination("Pokhara");
        pkg3.setDifficulty("EASY");
        pkg3.setSeason("ALL_YEAR");
        pkg3.setMaxParticipants(15);
        packages.add(pkg3);

        return packages;
    }

    private static void createDefaultDiscounts() {
        try {
            List<FestivalDiscount> defaultDiscounts = new ArrayList<>();

            FestivalDiscount discount1 = new FestivalDiscount();
            discount1.setDiscountId("DISC001");
            discount1.setFestivalName("Dashain Festival");
            discount1.setDiscountPercentage(15.0);
            discount1.setActive(true);
            defaultDiscounts.add(discount1);

            FestivalDiscount discount2 = new FestivalDiscount();
            discount2.setDiscountId("DISC002");
            discount2.setFestivalName("Tihar Festival");
            discount2.setDiscountPercentage(10.0);
            discount2.setActive(true);
            defaultDiscounts.add(discount2);

            FestivalDiscount discount3 = new FestivalDiscount();
            discount3.setDiscountId("DISC003");
            discount3.setFestivalName("New Year Special");
            discount3.setDiscountPercentage(20.0);
            discount3.setActive(false);
            defaultDiscounts.add(discount3);

            saveDiscounts(defaultDiscounts);

        } catch (Exception e) {
            System.err.println("Error creating default discounts: " + e.getMessage());
        }
    }

    // ==================== BACKUP AND UTILITY OPERATIONS ====================

    public static boolean createBackup() {
        try {
            // Create backup directory if it doesn't exist
            Files.createDirectories(Paths.get(BACKUP_DIR));

            // Create backup filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFileName = BACKUP_DIR + "tourism_backup_" + timestamp + ".zip";

            // Create zip file with all data files
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFileName))) {

                // Add all data files to zip
                addFileToZip(zos, USERS_FILE, "users.txt");
                addFileToZip(zos, TOURISTS_FILE, "tourists.txt");
                addFileToZip(zos, GUIDES_FILE, "guides.txt");
                addFileToZip(zos, PACKAGES_FILE, "packages.txt");
                addFileToZip(zos, BOOKINGS_FILE, "bookings.txt");
                addFileToZip(zos, DISCOUNTS_FILE, "discounts.txt");
                addFileToZip(zos, ACTIVITY_LOG_FILE, "activity.log");

            }

            logActivity("SYSTEM", "Backup created: " + backupFileName);
            System.out.println("Backup created successfully: " + backupFileName);
            return true;

        } catch (Exception e) {
            System.err.println("Error creating backup: " + e.getMessage());
            logActivity("SYSTEM", "Backup creation failed: " + e.getMessage());
            return false;
        }
    }

    private static void addFileToZip(ZipOutputStream zos, String filePath, String entryName) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            zos.putNextEntry(new ZipEntry(entryName));
            Files.copy(Paths.get(filePath), zos);
            zos.closeEntry();
        }
    }

    public static boolean restoreBackup(String backupFilePath) {
        try {
            logActivity("SYSTEM", "Backup restore attempted: " + backupFilePath);
            return true;
        } catch (Exception e) {
            System.err.println("Error restoring backup: " + e.getMessage());
            return false;
        }
    }

    public static void clearAllData() {
        try {
            // Clear all data files
            Files.deleteIfExists(Paths.get(USERS_FILE));
            Files.deleteIfExists(Paths.get(TOURISTS_FILE));
            Files.deleteIfExists(Paths.get(GUIDES_FILE));
            Files.deleteIfExists(Paths.get(PACKAGES_FILE));
            Files.deleteIfExists(Paths.get(BOOKINGS_FILE));
            Files.deleteIfExists(Paths.get(DISCOUNTS_FILE));

            logActivity("SYSTEM", "All data cleared");
            System.out.println("All data cleared successfully");

        } catch (Exception e) {
            System.err.println("Error clearing data: " + e.getMessage());
        }
    }

    public static long getDataSize() {
        try {
            long totalSize = 0;
            if (Files.exists(Paths.get(USERS_FILE))) totalSize += Files.size(Paths.get(USERS_FILE));
            if (Files.exists(Paths.get(TOURISTS_FILE))) totalSize += Files.size(Paths.get(TOURISTS_FILE));
            if (Files.exists(Paths.get(GUIDES_FILE))) totalSize += Files.size(Paths.get(GUIDES_FILE));
            if (Files.exists(Paths.get(PACKAGES_FILE))) totalSize += Files.size(Paths.get(PACKAGES_FILE));
            if (Files.exists(Paths.get(BOOKINGS_FILE))) totalSize += Files.size(Paths.get(BOOKINGS_FILE));
            if (Files.exists(Paths.get(DISCOUNTS_FILE))) totalSize += Files.size(Paths.get(DISCOUNTS_FILE));
            return totalSize;
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== DISCOUNT OPERATIONS ====================

    public static List<FestivalDiscount> getActiveDiscounts() {
        List<FestivalDiscount> discounts = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(DISCOUNTS_FILE))) {
                createDefaultDiscounts();
            }

            List<String> lines = Files.readAllLines(Paths.get(DISCOUNTS_FILE));
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    FestivalDiscount discount = parseDiscountFromString(line);
                    if (discount != null && discount.isActive()) {
                        discounts.add(discount);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading active discounts: " + e.getMessage());
        }
        return discounts;
    }

    public static List<FestivalDiscount> getAllDiscounts() {
        List<FestivalDiscount> discounts = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(DISCOUNTS_FILE))) {
                createDefaultDiscounts();
            }

            List<String> lines = Files.readAllLines(Paths.get(DISCOUNTS_FILE));
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    FestivalDiscount discount = parseDiscountFromString(line);
                    if (discount != null) {
                        discounts.add(discount);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading discounts: " + e.getMessage());
        }
        return discounts;
    }

    public static boolean saveDiscounts(List<FestivalDiscount> discounts) {
        try {
            List<String> lines = new ArrayList<>();
            for (FestivalDiscount discount : discounts) {
                lines.add(discountToString(discount));
            }
            Files.write(Paths.get(DISCOUNTS_FILE), lines);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving discounts: " + e.getMessage());
            return false;
        }
    }

    public static FestivalDiscount findDiscountById(String discountId) {
        List<FestivalDiscount> discounts = getAllDiscounts();
        return discounts.stream()
                .filter(discount -> discountId.equals(discount.getDiscountId()))
                .findFirst()
                .orElse(null);
    }

    public static boolean saveDiscount(FestivalDiscount discount) {
        try {
            List<FestivalDiscount> discounts = getAllDiscounts();
            discounts.removeIf(d -> discount.getDiscountId().equals(d.getDiscountId()));
            discounts.add(discount);
            return saveDiscounts(discounts);
        } catch (Exception e) {
            System.err.println("Error saving discount: " + e.getMessage());
            return false;
        }
    }

    private static String discountToString(FestivalDiscount discount) {
        StringBuilder sb = new StringBuilder();
        sb.append(discount.getDiscountId() != null ? discount.getDiscountId() : "").append("|");
        sb.append(discount.getFestivalName() != null ? discount.getFestivalName() : "").append("|");
        sb.append(discount.getDiscountPercentage()).append("|"); // Remove null check for primitive double
        sb.append(String.valueOf(discount.isActive()));
        return sb.toString();
    }

    private static FestivalDiscount parseDiscountFromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 3) {
                FestivalDiscount discount = new FestivalDiscount();
                discount.setDiscountId(parts[0]);
                discount.setFestivalName(parts[1]);

                if (!parts[2].isEmpty()) {
                    try {
                        discount.setDiscountPercentage(Double.parseDouble(parts[2]));
                    } catch (NumberFormatException e) {
                        discount.setDiscountPercentage(0.0);
                    }
                }

                if (parts.length > 3) {
                    discount.setActive(Boolean.parseBoolean(parts[3]));
                } else {
                    discount.setActive(true);
                }

                return discount;
            }
        } catch (Exception e) {
            System.err.println("Error parsing discount: " + line);
        }
        return null;
    }

    // ==================== USER OPERATIONS ====================

    public static boolean saveUser(User user) {
        try {
            List<User> users = getAllUsers();
            users.removeIf(u -> user.getUserId().equals(u.getUserId()));
            users.add(user);
            return saveUsers(users);
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveUsers(List<User> users) {
        try {
            List<String> lines = new ArrayList<>();
            for (User user : users) {
                lines.add(userToString(user));
            }
            Files.write(Paths.get(USERS_FILE), lines);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
        }
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(USERS_FILE))) {
                return users;
            }

            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    User user = parseUserFromString(line);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static User findUserByUsername(String username) {
        List<User> users = getAllUsers();
        return users.stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

    // ==================== TOURIST OPERATIONS ====================

    public static boolean saveTourist(Tourist tourist) {
        try {
            List<Tourist> tourists = getAllTourists();
            tourists.removeIf(t -> tourist.getTouristId().equals(t.getTouristId()));
            tourists.add(tourist);
            return saveTourists(tourists);
        } catch (Exception e) {
            System.err.println("Error saving tourist: " + e.getMessage());
            return false;
        }
    }

    public static List<Tourist> getAllTourists() {
        List<Tourist> tourists = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(TOURISTS_FILE))) {
                return tourists;
            }

            List<String> lines = Files.readAllLines(Paths.get(TOURISTS_FILE));
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    Tourist tourist = parseTouristFromString(line);
                    if (tourist != null) {
                        tourists.add(tourist);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading tourists: " + e.getMessage());
        }
        return tourists;
    }

    public static boolean saveTourists(List<Tourist> tourists) {
        try {
            List<String> lines = new ArrayList<>();
            for (Tourist tourist : tourists) {
                lines.add(touristToString(tourist));
            }
            Files.write(Paths.get(TOURISTS_FILE), lines);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving tourists: " + e.getMessage());
            return false;
        }
    }

    public static Tourist findTouristById(String touristId) {
        List<Tourist> tourists = getAllTourists();
        return tourists.stream()
                .filter(tourist -> touristId.equals(tourist.getTouristId()))
                .findFirst()
                .orElse(null);
    }

    // ==================== GUIDE OPERATIONS ====================

    public static List<Guide> getAllGuides() {
        List<Guide> guides = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(GUIDES_FILE))) {
                return guides;
            }

            List<String> lines = Files.readAllLines(Paths.get(GUIDES_FILE));
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    Guide guide = parseGuideFromString(line);
                    if (guide != null) {
                        guides.add(guide);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load guides: " + e.getMessage());
        }
        return guides;
    }

    public static void saveGuides(List<Guide> guides) {
        try {
            List<String> lines = new ArrayList<>();
            for (Guide guide : guides) {
                lines.add(guideToString(guide));
            }
            Files.write(Paths.get(GUIDES_FILE), lines);
        } catch (Exception e) {
            System.err.println("Error saving guides: " + e.getMessage());
        }
    }

    public static Guide findGuideById(String guideId) {
        List<Guide> guides = getAllGuides();
        return guides.stream()
                .filter(guide -> guideId.equals(guide.getGuideId()))
                .findFirst()
                .orElse(null);
    }

    // ==================== PACKAGE OPERATIONS ====================

    public static List<TourPackage> getAllPackages() {
        List<TourPackage> packages = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(PACKAGES_FILE))) {
                return packages;
            }

            List<String> lines = Files.readAllLines(Paths.get(PACKAGES_FILE));
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    TourPackage pkg = parsePackageFromString(line);
                    if (pkg != null) {
                        packages.add(pkg);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading packages: " + e.getMessage());
        }
        return packages;
    }

    public static boolean savePackages(List<TourPackage> packages) {
        try {
            List<String> lines = new ArrayList<>();
            for (TourPackage pkg : packages) {
                lines.add(packageToString(pkg));
            }
            Files.write(Paths.get(PACKAGES_FILE), lines);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving packages: " + e.getMessage());
            return false;
        }
    }

    public static TourPackage findTourPackageById(String packageId) {
        List<TourPackage> packages = getAllPackages();
        return packages.stream()
                .filter(pkg -> packageId.equals(pkg.getPackageId()))
                .findFirst()
                .orElse(null);
    }

    // ==================== BOOKING OPERATIONS ====================

    public static boolean saveBooking(Booking booking) {
        try {
            List<Booking> bookings = getAllBookings();
            bookings.removeIf(b -> booking.getBookingId().equals(b.getBookingId()));
            bookings.add(booking);

            List<String> lines = new ArrayList<>();
            for (Booking b : bookings) {
                lines.add(bookingToString(b));
            }
            Files.write(Paths.get(BOOKINGS_FILE), lines);

            logActivity("SYSTEM", "Booking saved: " + booking.getBookingId());
            return true;
        } catch (Exception e) {
            System.err.println("Error saving booking: " + e.getMessage());
            return false;
        }
    }

    public static List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(BOOKINGS_FILE))) {
                return bookings;
            }

            List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    Booking booking = parseBookingFromString(line);
                    if (booking != null) {
                        bookings.add(booking);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
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

    public static boolean deleteBooking(String bookingId) {
        try {
            List<Booking> bookings = getAllBookings();
            boolean removed = bookings.removeIf(booking -> bookingId.equals(booking.getBookingId()));

            if (removed) {
                List<String> lines = new ArrayList<>();
                for (Booking booking : bookings) {
                    lines.add(bookingToString(booking));
                }
                Files.write(Paths.get(BOOKINGS_FILE), lines);

                logActivity("SYSTEM", "Booking deleted: " + bookingId);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }

    // ==================== ACTIVITY LOGGING ====================

    public static void logActivity(String username, String activity) {
        try {
            String logEntry = LocalDateTime.now().format(DATE_FORMATTER) + " | " + username + " | " + activity;
            Files.write(Paths.get(ACTIVITY_LOG_FILE), (logEntry + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Error logging activity: " + e.getMessage());
        }
    }

    // ==================== STRING CONVERSION METHODS ====================

    private static String userToString(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUserId() != null ? user.getUserId() : "").append("|");
        sb.append(user.getUsername() != null ? user.getUsername() : "").append("|");
        sb.append(user.getPassword() != null ? user.getPassword() : "").append("|");
        sb.append(user.getRole() != null ? user.getRole() : "").append("|");
        sb.append(user.getFullName() != null ? user.getFullName() : "").append("|");
        sb.append(user.getAdminLevel() != null ? user.getAdminLevel() : "").append("|");
        sb.append(String.valueOf(user.isActive()));
        return sb.toString();
    }

    private static User parseUserFromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 4) {
                User user = new User(parts[0], parts[1], parts[2], parts[3]);
                if (parts.length > 4 && !parts[4].isEmpty()) user.setFullName(parts[4]);
                if (parts.length > 5 && !parts[5].isEmpty()) user.setAdminLevel(parts[5]);
                if (parts.length > 6) user.setActive(Boolean.parseBoolean(parts[6]));
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error parsing user: " + line);
        }
        return null;
    }

    private static String touristToString(Tourist tourist) {
        StringBuilder sb = new StringBuilder();
        sb.append(tourist.getTouristId() != null ? tourist.getTouristId() : "").append("|");
        sb.append(tourist.getAccountId() != null ? tourist.getAccountId() : "").append("|");
        sb.append(tourist.getFullName() != null ? tourist.getFullName() : "").append("|");
        sb.append(tourist.getEmail() != null ? tourist.getEmail() : "").append("|");
        sb.append(tourist.getPhoneNumber() != null ? tourist.getPhoneNumber() : "").append("|");
        sb.append(tourist.getNationality() != null ? tourist.getNationality() : "").append("|");
        sb.append(String.valueOf(tourist.isActive()));
        return sb.toString();
    }

    private static Tourist parseTouristFromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 3) {
                Tourist tourist = new Tourist();
                tourist.setTouristId(parts[0]);
                if (parts.length > 1 && !parts[1].isEmpty()) tourist.setAccountId(parts[1]);
                if (parts.length > 2 && !parts[2].isEmpty()) tourist.setFullName(parts[2]);
                if (parts.length > 3 && !parts[3].isEmpty()) tourist.setEmail(parts[3]);
                if (parts.length > 4 && !parts[4].isEmpty()) tourist.setPhoneNumber(parts[4]);
                if (parts.length > 5 && !parts[5].isEmpty()) tourist.setNationality(parts[5]);
                if (parts.length > 6) tourist.setActive(Boolean.parseBoolean(parts[6]));
                return tourist;
            }
        } catch (Exception e) {
            System.err.println("Error parsing tourist: " + line);
        }
        return null;
    }

    private static String guideToString(Guide guide) {
        StringBuilder sb = new StringBuilder();
        sb.append(guide.getGuideId() != null ? guide.getGuideId() : "").append("|");
        sb.append(guide.getFullName() != null ? guide.getFullName() : "").append("|");
        sb.append(guide.getEmail() != null ? guide.getEmail() : "").append("|");
        sb.append(guide.getPhoneNumber() != null ? guide.getPhoneNumber() : "").append("|");
        sb.append(guide.getSpecialization() != null ? guide.getSpecialization() : "").append("|");
        sb.append(String.valueOf(guide.isActive()));
        return sb.toString();
    }

    private static Guide parseGuideFromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 2) {
                Guide guide = new Guide();
                guide.setGuideId(parts[0]);
                if (parts.length > 1 && !parts[1].isEmpty()) guide.setFullName(parts[1]);
                if (parts.length > 2 && !parts[2].isEmpty()) guide.setEmail(parts[2]);
                if (parts.length > 3 && !parts[3].isEmpty()) guide.setPhoneNumber(parts[3]);
                if (parts.length > 4 && !parts[4].isEmpty()) guide.setSpecialization(parts[4]);
                if (parts.length > 5) guide.setActive(Boolean.parseBoolean(parts[5]));
                return guide;
            }
        } catch (Exception e) {
            System.err.println("Error parsing guide: " + line);
        }
        return null;
    }

    private static String packageToString(TourPackage pkg) {
        StringBuilder sb = new StringBuilder();
        sb.append(pkg.getPackageId() != null ? pkg.getPackageId() : "").append("|");
        sb.append(pkg.getPackageName() != null ? pkg.getPackageName() : "").append("|");
        sb.append(pkg.getDescription() != null ? pkg.getDescription() : "").append("|");
        sb.append(pkg.getPrice() != null ? pkg.getPrice().toString() : "0.0").append("|");
        sb.append(pkg.getDuration() != null ? pkg.getDuration().toString() : "1").append("|");
        sb.append(pkg.getCategory() != null ? pkg.getCategory() : "").append("|");
        sb.append(pkg.getDestination() != null ? pkg.getDestination() : "").append("|");
        sb.append(pkg.getDifficulty() != null ? pkg.getDifficulty() : "").append("|");
        sb.append(pkg.getSeason() != null ? pkg.getSeason() : "").append("|");
        sb.append(pkg.getMaxParticipants() != null ? pkg.getMaxParticipants().toString() : "0").append("|");
        sb.append(String.valueOf(pkg.isActive()));
        return sb.toString();
    }

    private static TourPackage parsePackageFromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                TourPackage pkg = new TourPackage();

                pkg.setPackageId(parts[0]);
                pkg.setPackageName(parts[1]);
                pkg.setDescription(parts[2]);

                // Parse price safely
                if (!parts[3].isEmpty()) {
                    try {
                        pkg.setPrice(Double.parseDouble(parts[3]));
                    } catch (NumberFormatException e) {
                        pkg.setPrice(0.0);
                    }
                }

                // Parse duration safely
                if (!parts[4].isEmpty()) {
                    try {
                        pkg.setDuration(Integer.parseInt(parts[4]));
                    } catch (NumberFormatException e) {
                        pkg.setDuration(1);
                    }
                }

                pkg.setCategory(parts[5]);

                // Optional fields
                if (parts.length > 6 && !parts[6].isEmpty()) pkg.setDestination(parts[6]);
                if (parts.length > 7 && !parts[7].isEmpty()) pkg.setDifficulty(parts[7]);
                if (parts.length > 8 && !parts[8].isEmpty()) pkg.setSeason(parts[8]);

                if (parts.length > 9 && !parts[9].isEmpty()) {
                    try {
                        pkg.setMaxParticipants(Integer.parseInt(parts[9]));
                    } catch (NumberFormatException e) {
                        pkg.setMaxParticipants(0);
                    }
                }

                if (parts.length > 10) {
                    pkg.setActive(Boolean.parseBoolean(parts[10]));
                } else {
                    pkg.setActive(true);
                }

                return pkg;
            }
        } catch (Exception e) {
            System.err.println("Error parsing package: " + line + " - " + e.getMessage());
        }
        return null;
    }

    private static String bookingToString(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append(booking.getBookingId() != null ? booking.getBookingId() : "").append("|");
        sb.append(booking.getTouristId() != null ? booking.getTouristId() : "").append("|");
        sb.append(booking.getPackageId() != null ? booking.getPackageId() : "").append("|");
        sb.append(booking.getGuideId() != null ? booking.getGuideId() : "").append("|");
        sb.append(booking.getStatus() != null ? booking.getStatus() : "PENDING").append("|");
        sb.append(booking.getTotalAmount() != null ? booking.getTotalAmount().toString() : "0.0").append("|");
        sb.append(booking.getNumberOfPeople() != null ? booking.getNumberOfPeople().toString() : "1").append("|");
        sb.append(booking.getSpecialRequests() != null ? booking.getSpecialRequests() : "").append("|");
        sb.append(booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "PENDING");
        return sb.toString();
    }

    private static Booking parseBookingFromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                Booking booking = new Booking();
                booking.setBookingId(parts[0]);
                booking.setTouristId(parts[1]);
                booking.setPackageId(parts[2]);
                if (!parts[3].isEmpty()) booking.setGuideId(parts[3]);
                booking.setStatus(parts[4]);

                if (parts.length > 5 && !parts[5].isEmpty()) {
                    try {
                        booking.setTotalAmount(Double.parseDouble(parts[5]));
                    } catch (NumberFormatException e) {
                        booking.setTotalAmount(0.0);
                    }
                }

                if (parts.length > 6 && !parts[6].isEmpty()) {
                    try {
                        booking.setNumberOfPeople(Integer.parseInt(parts[6]));
                    } catch (NumberFormatException e) {
                        booking.setNumberOfPeople(1);
                    }
                }

                if (parts.length > 7) booking.setSpecialRequests(parts[7]);
                if (parts.length > 8) booking.setPaymentStatus(parts[8]);

                return booking;
            }
        } catch (Exception e) {
            System.err.println("Error parsing booking: " + line);
        }
        return null;
    }
}