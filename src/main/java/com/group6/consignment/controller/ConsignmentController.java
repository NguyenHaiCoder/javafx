package com.group6.consignment.controller;

import com.group6.consignment.MainApp;
import com.group6.consignment.model.Car;
import com.group6.consignment.model.CarStore;
import javafx.beans.binding.BooleanBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConsignmentController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final PseudoClass INSPECTED = PseudoClass.getPseudoClass("inspected");

    @FXML private TextField plateField;
    @FXML private ComboBox<String> typeBox;
    @FXML private Button consignBtn;
    @FXML private TableView<Car> carTable;
    @FXML private TableColumn<Car, String> plateCol;
    @FXML private TableColumn<Car, String> typeCol;
    @FXML private TableColumn<Car, String> dateCol;
    @FXML private TableColumn<Car, String> timeCol;
    @FXML private TableColumn<Car, String> createdCol;
    @FXML private TableColumn<Car, String> updatedCol;
    @FXML private Button inspectBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Label statusLabel;

    private final CarStore store = CarStore.getInstance();

    // Called by FXMLLoader after all @FXML fields are injected
    @FXML
    private void initialize() {
        typeBox.getItems().addAll("Sedan", "SUV", "Pickup", "Hatchback", "MPV");
        typeBox.setValue("SUV");
        // Columns read Car properties via lambdas (no PropertyValueFactory); map() formats and stays live
        plateCol.setCellValueFactory(cell -> cell.getValue().plateProperty());
        typeCol.setCellValueFactory(cell -> cell.getValue().typeProperty());
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty().map(DATE_FORMAT::format));
        timeCol.setCellValueFactory(cell -> cell.getValue().timeInProperty().map(TIME_FORMAT::format));
        createdCol.setCellValueFactory(cell -> cell.getValue().createdAtProperty().map(DATE_TIME_FORMAT::format));
        updatedCol.setCellValueFactory(cell -> cell.getValue().updatedAtProperty().map(DATE_TIME_FORMAT::format));
        carTable.setItems(store.getCars());
        carTable.setRowFactory(table -> {
            TableRow<Car> row = new TableRow<>() {
                @Override
                protected void updateItem(Car car, boolean empty) {
                    super.updateItem(car, empty);
                    pseudoClassStateChanged(INSPECTED, !empty && car.isInspected()); // CSS :inspected -> green row
                }
            };
            // Event: double-click a row to open Inspection
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    MainApp.showInspection(row.getItem());
                }
            });
            return row;
        });

        // Binding: button disabled while field is empty
        consignBtn.disableProperty().bind(plateField.textProperty().isEmpty());
        // Binding: row actions disabled until a row is selected
        BooleanBinding noSelection = carTable.getSelectionModel().selectedItemProperty().isNull();
        inspectBtn.disableProperty().bind(noSelection);
        editBtn.disableProperty().bind(noSelection);
        deleteBtn.disableProperty().bind(noSelection);

        // Listener: uppercase the plate while typing
        plateField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(newText.toUpperCase())) {
                plateField.setText(newText.toUpperCase());
            }
        });
    }

    // Event handler: Consign button (Enter also works, it is the default button)
    @FXML
    private void handleConsign() {
        String plate = plateField.getText().trim().toUpperCase();
        if (!isValidPlate(plate, null)) {
            return;
        }
        Car car = new Car(plate, typeBox.getValue(), LocalDate.now(), LocalTime.now().withNano(0));
        store.getCars().add(car);
        carTable.getSelectionModel().select(car);
        carTable.scrollTo(car);
        plateField.clear();
        statusLabel.setText(plate + " consigned at " + car.getTimeIn().format(TIME_FORMAT));
    }

    // Event handler: Inspect Selected button
    @FXML
    private void handleInspect() {
        MainApp.showInspection(carTable.getSelectionModel().getSelectedItem());
    }

    // Event handler: Edit plate and type of the selected car in a small dialog
    @FXML
    private void handleEdit() {
        Car car = carTable.getSelectionModel().getSelectedItem();
        TextField plateInput = new TextField(car.getPlate());
        ComboBox<String> typeInput = new ComboBox<>(typeBox.getItems());
        typeInput.setValue(car.getType());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit " + car.getPlate());
        dialog.getDialogPane().setContent(new VBox(8,
                new Label("License Plate"), plateInput, new Label("Car Type"), typeInput));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (MainApp.showDialog(dialog) != ButtonType.OK) {
            return;
        }
        String plate = plateInput.getText().trim().toUpperCase();
        if (isValidPlate(plate, car)) {
            car.setPlate(plate); // property changes -> table cells update themselves
            car.setType(typeInput.getValue());
            statusLabel.setText(plate + " updated");
        }
    }

    // Event handler: Delete the selected car after confirmation
    @FXML
    private void handleDelete() {
        Car car = carTable.getSelectionModel().getSelectedItem();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + car.getPlate() + "?");
        if (MainApp.showDialog(confirm) == ButtonType.OK) {
            store.getCars().remove(car); // ObservableList change -> row disappears
            statusLabel.setText(car.getPlate() + " deleted");
        }
    }

    // Shows a warning and returns false if the plate has a wrong format or is already used
    private boolean isValidPlate(String plate, Car self) {
        String error = store.validatePlate(plate, self);
        if (error != null) {
            MainApp.showDialog(new Alert(Alert.AlertType.WARNING, error));
        }
        return error == null;
    }
}
