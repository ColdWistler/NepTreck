module com.tourism {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Open packages for FXML to allow reflective access for controllers
    opens com.tourism.controllers to javafx.fxml;
    opens com.tourism.models to javafx.fxml; // If models are accessed by FXML or controllers via reflection
    opens com.tourism.services to javafx.fxml; // If services are accessed by FXML or controllers via reflection
    opens com.tourism.utils to javafx.fxml; // If utils are accessed by FXML or controllers via reflection

    // EXPORT the main package so JavaFX can access your TourismApp class and other core components.
    // This is the crucial fix for the IllegalAccessError you are getting.
    exports com.tourism;
}
