package com.group6.consignment.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;

// Data shared by both screens, saved to a text file so it survives restarts (no database)
public final class CarStore {

    // One file per Windows user: <user home>/car-consignment/cars.txt
    private static final Path DATA_FILE = Path.of(System.getProperty("user.home"), "car-consignment", "cars.txt");
    private static final Pattern PLATE_PATTERN = Pattern.compile("^\\d{2}[A-Z]-\\d{3}\\.\\d{2}$");
    private static final CarStore INSTANCE = new CarStore();

    // ObservableList with an extractor: it also reports edits inside a Car, not only add/remove
    private final ObservableList<Car> cars = FXCollections.observableArrayList(car -> new Observable[] {
            car.plateProperty(), car.typeProperty(), car.inspectedProperty(), car.notesProperty(),
            car.checkedItemsProperty(), car.updatedAtProperty()});

    private CarStore() {
        if (Files.exists(DATA_FILE)) {
            load();
        } else {
            LocalDate today = LocalDate.now();
            cars.add(new Car("51G-123.45", "Sedan", today, LocalTime.of(8, 15)));
            cars.add(new Car("30A-567.89", "Pickup", today, LocalTime.of(8, 42)));
            save();
        }
        // Any add / edit / delete / inspection -> write the file again
        cars.addListener((ListChangeListener<Car>) change -> save());
    }

    public static CarStore getInstance() {
        return INSTANCE;
    }

    public ObservableList<Car> getCars() {
        return cars;
    }

    // Returns an error message, or null if the plate is OK ("self" = car being edited, or null)
    public String validatePlate(String plate, Car self) {
        if (!PLATE_PATTERN.matcher(plate).matches()) {
            return "Invalid plate. Use the format 51K-234.56";
        }
        boolean taken = cars.stream().anyMatch(car -> car != self && car.getPlate().equals(plate));
        return taken ? plate + " is already consigned." : null;
    }

    // One line per car: plate|type|date|timeIn|inspected|createdAt|updatedAt|notes|checkedItems (";"-separated)
    private void save() {
        List<String> lines = cars.stream().map(car -> String.join("|", car.getPlate(), car.getType(),
                car.getDate().toString(), car.getTimeIn().toString(), String.valueOf(car.isInspected()),
                car.getCreatedAt().toString(), car.getUpdatedAt().toString(),
                car.getNotes().replace("|", "/").replace("\n", "\\n"),
                String.join(";", car.getCheckedItems()))).toList();
        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.write(DATA_FILE, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save " + DATA_FILE, e);
        }
    }

    private void load() {
        try {
            for (String line : Files.readAllLines(DATA_FILE)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] f = line.split("\\|", -1);
                Car car = new Car(f[0], f[1], LocalDate.parse(f[2]), LocalTime.parse(f[3]));
                car.setInspected(Boolean.parseBoolean(f[4]));
                car.setNotes(f[7].replace("\\n", "\n"));
                // Lines saved before the checklist was stored have only 8 fields
                car.setCheckedItems(f.length > 8 && !f[8].isEmpty() ? List.of(f[8].split(";")) : List.of());
                car.setCreatedAt(LocalDateTime.parse(f[5]));
                car.setUpdatedAt(LocalDateTime.parse(f[6])); // last, so the setters above don't overwrite it
                cars.add(car);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + DATA_FILE, e);
        }
    }
}
