package com.tourism;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.tourism.utils.FileDataManager;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class TourismApp extends Application {

    private static Stage primaryStage;
    // APP_TITLE and APP_VERSION can now be pulled from ResourceBundle for localization
    // private static final String APP_TITLE = "VisiNepalTech Tourism Management System";
    // private static final String APP_VERSION = "v1.0.0";

    private static Locale currentLocale; // Static field to hold the current locale for the application

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Store the primary stage reference

        // Set the initial locale (e.g., system default or a fixed default like English)
        if (currentLocale == null) {
            currentLocale = Locale.getDefault(); // Or Locale.ENGLISH for a fixed default
        }

        // Initialize application (Your existing method)
        initializeApplication();

        // Load and show login screen with localization
        showLoginScreenLocalized();

        // Configure primary stage (Your existing method, but title will be set from resource bundle)
        configurePrimaryStage();

        // Show the application
        primaryStage.show();

        // Log application startup
        FileDataManager.logActivity("SYSTEM", "Application started successfully");
    }

    private void initializeApplication() {
        try {
            // Use ResourceBundle for app title and version if desired, or keep as constants
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
            System.out.println("=== " + bundle.getString("app.brandTitle") + " " + bundle.getString("app.version") + " ==="); // Add app.version to your messages.properties
            System.out.println("Initializing application...");

        } catch (Exception e) {
            System.err.println("Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void showLoginScreenLocalized() throws IOException {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), bundle);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);

            // Load CSS stylesheet
            String css = Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            // Set title from the resource bundle
            primaryStage.setTitle(bundle.getString("app.brandTitle") + " - " + bundle.getString("login.title"));

            System.out.println("Login screen loaded successfully with locale: " + currentLocale.getDisplayName());

        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            throw e;
        }
    }

    private void configurePrimaryStage() {
        try {


            try {
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/app-icon.png")));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("Application icon not found, using default");
            }

            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();

            primaryStage.setOnCloseRequest(event -> {
                handleApplicationExit();
            });

            primaryStage.setResizable(true);

        } catch (Exception e) {
            System.err.println("Failed to configure primary stage: " + e.getMessage());
        }
    }

    private void handleApplicationExit() {
        try {
            FileDataManager.logActivity("SYSTEM", "Application shutting down");
            boolean backupCreated = FileDataManager.createBackup();
            if (backupCreated) {
                System.out.println("Backup created successfully before exit");
            }
            System.out.println("Application closed successfully");
        } catch (Exception e) {
            System.err.println("Error during application shutdown: " + e.getMessage());
        }
    }


    public static Locale getLocale() {
        return currentLocale;
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        try {
            // For a general solution, you'd track the currently loaded FXML path.
            // For this specific login scenario, we reload login.fxml.
            String currentFxmlPath = "/fxml/login.fxml";

            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
            FXMLLoader loader = new FXMLLoader(TourismApp.class.getResource(currentFxmlPath), bundle);
            Parent root = loader.load();
            Scene scene = new Scene(root); // Assuming current scene dimensions are fine, or pass them
            primaryStage.setScene(scene);
            primaryStage.setTitle(bundle.getString("app.brandTitle") + " - " + bundle.getString("login.title")); // Update stage title with new locale
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error setting new locale and reloading stage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchScene(String fxmlPath, String title, double width, double height) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
            FXMLLoader loader = new FXMLLoader(TourismApp.class.getResource(fxmlPath), bundle);
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);

            String css = Objects.requireNonNull(TourismApp.class.getResource("/css/styles.css")).toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            // Use the provided title directly if it's already localized, or get from bundle
            primaryStage.setTitle(bundle.getString("app.brandTitle") + " - " + title); // Assuming 'title' parameter itself might be a key or already localized.
            primaryStage.centerOnScreen();

            FileDataManager.logActivity("SYSTEM", "Scene switched to: " + fxmlPath);

        } catch (IOException e) {
            System.err.println("Failed to switch scene to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchScene(String fxmlPath, String title) {
        switchScene(fxmlPath, title, 1000, 700);
    }

    public static void main(String[] args) {
        try {
            // Initializing with a default locale before launch to ensure bundle is ready
            currentLocale = Locale.getDefault(); // Set a default locale early
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
            System.out.println("Starting " + bundle.getString("app.brandTitle") + " " + bundle.getString("app.version") + "...");
            launch(args);
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
