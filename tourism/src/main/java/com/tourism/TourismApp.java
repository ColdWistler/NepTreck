package com.tourism;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.tourism.utils.FileDataManager;

import java.io.IOException;
import java.util.Objects;

public class TourismApp extends Application {

    private static Stage primaryStage;
    private static final String APP_TITLE = "VisiNepalTech Tourism Management System";
    private static final String APP_VERSION = "v1.0.0";

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initialize application
        initializeApplication();

        // Load and show login screen
        showLoginScreen();

        // Configure primary stage
        configurePrimaryStage();

        // Show the application
        primaryStage.show();

        // Log application startup
        FileDataManager.logActivity("SYSTEM", "Application started successfully");
    }

    private void initializeApplication() {
        try {
            System.out.println("=== " + APP_TITLE + " " + APP_VERSION + " ===");
            System.out.println("Initializing application...");

        } catch (Exception e) {
            System.err.println("Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showLoginScreen() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);

            // Load CSS stylesheet
            String css = Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);

            System.out.println("Login screen loaded successfully");

        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            throw e;
        }
    }

    private void configurePrimaryStage() {
        try {
            primaryStage.setTitle(APP_TITLE + " " + APP_VERSION);

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

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(String fxmlPath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(TourismApp.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);

            String css = Objects.requireNonNull(TourismApp.class.getResource("/css/styles.css")).toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            primaryStage.setTitle(APP_TITLE + " - " + title);
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
            System.out.println("Starting " + APP_TITLE + " " + APP_VERSION + "...");
            launch(args);
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}