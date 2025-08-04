package com.tourism.controllers;

import com.tourism.models.TourPackage;
import com.tourism.services.TourPackageService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class AttractionsController {

    @FXML
    private ListView<String> attractionsListView;

    private final TourPackageService packageService = new TourPackageService();

    public void initialize() {
        try {
            List<TourPackage> packages = packageService.getAllPackages();
            for (TourPackage pkg : packages) {
                String display = pkg.getPackageName()
                        + " | Destination: " + pkg.getDestination()
                        + " | Price: $" + pkg.getPrice()
                        + " | Duration: " + pkg.getDurationDays() + " days";
                attractionsListView.getItems().add(display);
            }
        } catch (Exception e) {
            attractionsListView.getItems().add("Failed to load packages: " + e.getMessage());
            System.err.println("Error loading packages: " + e.getMessage());
        }
    }
}
