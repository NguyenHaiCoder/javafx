package com.group6.consignment.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// JavaFX properties: the UI is notified automatically when a value changes
public class Car {

    private final StringProperty plate = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> timeIn = new SimpleObjectProperty<>();
    private final BooleanProperty inspected = new SimpleBooleanProperty(false);
    private final StringProperty notes = new SimpleStringProperty("");
    // Names of the checklist items ticked for this car, e.g. ["Body & paint", "Tires"]
    private final ObjectProperty<List<String>> checkedItems = new SimpleObjectProperty<>(List.of());
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    public Car(String plate, String type, LocalDate date, LocalTime timeIn) {
        setPlate(plate);
        setType(type);
        setDate(date);
        setTimeIn(timeIn);
        setCreatedAt(LocalDateTime.now().withNano(0));
        setUpdatedAt(getCreatedAt());

        // Any later edit or inspection refreshes updatedAt (like JPA @UpdateTimestamp)
        ChangeListener<Object> touch = (obs, oldValue, newValue) -> setUpdatedAt(LocalDateTime.now().withNano(0));
        this.plate.addListener(touch);
        this.type.addListener(touch);
        inspected.addListener(touch);
        notes.addListener(touch);
        checkedItems.addListener(touch);
    }

    public String getPlate() { return plate.get(); }
    public void setPlate(String value) { plate.set(value); }
    public StringProperty plateProperty() { return plate; }

    public String getType() { return type.get(); }
    public void setType(String value) { type.set(value); }
    public StringProperty typeProperty() { return type; }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate value) { date.set(value); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    public LocalTime getTimeIn() { return timeIn.get(); }
    public void setTimeIn(LocalTime value) { timeIn.set(value); }
    public ObjectProperty<LocalTime> timeInProperty() { return timeIn; }

    public boolean isInspected() { return inspected.get(); }
    public void setInspected(boolean value) { inspected.set(value); }
    public BooleanProperty inspectedProperty() { return inspected; }

    public String getNotes() { return notes.get(); }
    public void setNotes(String value) { notes.set(value); }
    public StringProperty notesProperty() { return notes; }

    public List<String> getCheckedItems() { return checkedItems.get(); }
    public void setCheckedItems(List<String> value) { checkedItems.set(List.copyOf(value)); }
    public ObjectProperty<List<String>> checkedItemsProperty() { return checkedItems; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime value) { updatedAt.set(value); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
}
