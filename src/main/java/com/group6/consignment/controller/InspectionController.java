package com.group6.consignment.controller;

import com.group6.consignment.MainApp;
import com.group6.consignment.model.Car;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.List;

public class InspectionController {

    @FXML private Label plateBadge;
    @FXML private CheckBox bodyCheck;
    @FXML private CheckBox mirrorsCheck;
    @FXML private CheckBox lightsCheck;
    @FXML private CheckBox tiresCheck;
    @FXML private CheckBox damageCheck;
    @FXML private CheckBox keysCheck;
    @FXML private Label progressLabel;
    @FXML private TextArea notesArea;

    private List<CheckBox> checks;
    private Car car;

    @FXML
    private void initialize() {
        checks = List.of(bodyCheck, mirrorsCheck, lightsCheck, tiresCheck, damageCheck, keysCheck);

        // Binding: "Checked x/6" is recomputed whenever any checkbox changes
        Observable[] selected = checks.stream().map(CheckBox::selectedProperty).toArray(Observable[]::new);
        progressLabel.textProperty().bind(Bindings.createStringBinding(
                () -> "Checked " + countChecked() + "/" + checks.size(), selected));
    }

    // Receives the car chosen on the Consignment screen (called by MainApp)
    public void setCar(Car car) {
        this.car = car;
        plateBadge.setText(car.getPlate());
        notesArea.setText(car.getNotes()); // previous notes if already inspected
        // Restore the ticks saved for this car
        checks.forEach(box -> box.setSelected(car.getCheckedItems().contains(box.getText())));
    }

    // Event handler: Save Inspection
    @FXML
    private void handleSave() {
        car.setNotes(notesArea.getText().trim());
        car.setCheckedItems(checks.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList());
        car.setInspected(true);

        String message = "Inspection saved for " + car.getPlate() + " — "
                + countChecked() + "/" + checks.size() + " items passed";
        List<String> unchecked = checks.stream().filter(box -> !box.isSelected()).map(CheckBox::getText).toList();
        if (!unchecked.isEmpty()) {
            message += "\n\nNot checked:\n• " + String.join("\n• ", unchecked);
        }

        MainApp.showDialog(new Alert(Alert.AlertType.INFORMATION, message));
        MainApp.showConsignment();
    }

    // Event handler: Back
    @FXML
    private void handleBack() {
        MainApp.showConsignment();
    }

    private long countChecked() {
        return checks.stream().filter(CheckBox::isSelected).count();
    }
}
