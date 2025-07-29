module com.tourism {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Open for FXML and PropertyValueFactory reflection
    opens com.tourism.controllers to javafx.fxml;
    opens com.tourism.models to javafx.fxml, javafx.base;
    opens com.tourism.services to javafx.fxml;
    opens com.tourism.utils to javafx.fxml;

    exports com.tourism;
}

