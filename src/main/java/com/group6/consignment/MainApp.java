package com.group6.consignment;

import com.group6.consignment.controller.InspectionController;
import com.group6.consignment.model.Car;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

public class MainApp extends Application {

    // One Scene for the whole app; navigation only swaps its root
    private static Scene scene;

    @Override
    public void start(Stage stage) {
        // Scene = content container, its root is loaded from FXML
        scene = new Scene(load(loader("consignment-view.fxml")), 1100, 680);
        scene.getStylesheets().add(MainApp.class.getResource("style.css").toExternalForm());

        // Stage = window
        stage.setTitle("Car Consignment Manager");
        stage.setMinWidth(960);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    // Navigation: same Stage, same Scene, new root from inspection-view.fxml
    public static void showInspection(Car car) {
        FXMLLoader loader = loader("inspection-view.fxml");
        Parent root = load(loader);
        InspectionController controller = loader.getController();
        controller.setCar(car);
        scene.setRoot(root);
    }

    public static void showConsignment() {
        scene.setRoot(load(loader("consignment-view.fxml")));
    }

    // Dialogs open over the main window and reuse the Scene's CSS
    public static ButtonType showDialog(Dialog<ButtonType> dialog) {
        dialog.setHeaderText(null);
        dialog.initOwner(scene.getWindow());
        dialog.getDialogPane().getStylesheets().addAll(scene.getStylesheets());
        return dialog.showAndWait().orElse(ButtonType.CANCEL);
    }

    private static FXMLLoader loader(String fxml) {
        return new FXMLLoader(MainApp.class.getResource(fxml));
    }

    private static Parent load(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load " + loader.getLocation(), e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
