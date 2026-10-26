module car.consignment {
    requires javafx.controls;
    requires javafx.fxml;

    // FXMLLoader injects @FXML fields and handlers into the controllers
    opens com.group6.consignment.controller to javafx.fxml;
    // JavaFX launcher creates MainApp
    exports com.group6.consignment;
}
