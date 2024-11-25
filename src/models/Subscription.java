package models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Subscription {

    private final SimpleIntegerProperty subscriptionID;
    private final SimpleStringProperty name;
    private final SimpleStringProperty description;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty durationInDays;
    private final SimpleIntegerProperty userID;

    // Constructor with all fields
    public Subscription(int subscriptionID, String name, String description, double price, int durationInDays, int userID) {
        this.subscriptionID = new SimpleIntegerProperty(subscriptionID);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.durationInDays = new SimpleIntegerProperty(durationInDays);
        this.userID = new SimpleIntegerProperty(userID);
    }

    // Constructor for creating new subscriptions without an ID
    public Subscription(String name, String description, double price, int durationInDays, int userID) {
        this.subscriptionID = new SimpleIntegerProperty(0); // Default ID for new subscriptions
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.durationInDays = new SimpleIntegerProperty(durationInDays);
        this.userID = new SimpleIntegerProperty(userID);
    }

    // Getters
    public int getSubscriptionID() {
        return subscriptionID.get();
    }

    public String getName() {
        return name.get();
    }

    public String getDescription() {
        return description.get();
    }

    public double getPrice() {
        return price.get();
    }

    public int getDurationInDays() {
        return durationInDays.get();
    }

    public int getUserID() {
        return userID.get();
    }

    // Properties for JavaFX bindings
    public SimpleIntegerProperty subscriptionIDProperty() {
        return subscriptionID;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public SimpleIntegerProperty durationInDaysProperty() {
        return durationInDays;
    }

    public SimpleIntegerProperty userIDProperty() {
        return userID;
    }
}
