package controllers;

import db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.SessionManager;
import models.Subscription;

import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;













import application.AppUtils;

public class SubscriptionController {

    @FXML
    private TableView<Subscription> subscriptionTable;

    @FXML
    private TableColumn<Subscription, String> nameColumn;

    @FXML
    private TableColumn<Subscription, String> descriptionColumn;

    @FXML
    private TableColumn<Subscription, Double> priceColumn;

    @FXML
    private TableColumn<Subscription, Integer> durationColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField durationField;

    @FXML
    private ImageView subscriptionImage;

    @FXML
    private AnchorPane rootPane;

    private ObservableList<Subscription> subscriptions = FXCollections.observableArrayList();
    private ObservableList<Subscription> searchResults = FXCollections.observableArrayList();

    private ObservableList<Subscription> mockupData = FXCollections.observableArrayList(
        new Subscription(101, "Netflix Standard", "Stream TV shows and movies in HD on two devices.", 15.49, 30, 0),
        new Subscription(102, "Spotify Premium Individual", "Ad-free music streaming with offline playback and unlimited skips.", 9.99, 30, 0),
        new Subscription(103, "Amazon Prime Monthly", "Access to Prime Video, free shipping, and exclusive deals.", 14.99, 30, 0),
        new Subscription(104, "Creative Cloud All Apps", "Access to 20+ creative apps, including Photoshop and Illustrator.", 54.99, 30, 0),
        new Subscription(105, "Disney+ Premium", "Ad-free streaming of Disney, Pixar, Marvel, and Star Wars content.", 10.99, 30, 0),
        new Subscription(106, "Hulu (No Ads)", "Stream TV shows and movies ad-free.", 14.99, 30, 0),
        new Subscription(107, "Apple Music Individual Plan", "Access to over 100 million songs, ad-free with offline listening.", 10.99, 30, 0),
        new Subscription(108, "Microsoft 365 Personal", "Access to Office apps, 1 TB of OneDrive storage, and advanced security.", 6.99, 30, 0),
        new Subscription(109, "YouTube Premium", "Ad-free videos, background play, and offline downloads.", 13.99, 30, 0),
        new Subscription(110, "Zoom Pro Plan", "Host unlimited group meetings up to 30 hours with cloud recording.", 14.99, 30, 0)
    );

    private int currentUserId; 

    @FXML
    public void initialize() {
        this.currentUserId = SessionManager.getInstance().getCurrentUserId();
        System.out.println("SubscriptionController: Retrieved currentUserId from SessionManager = " + this.currentUserId);

        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationInDaysProperty().asObject());

        loadActiveSubscriptions();
    }


    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("Current User ID set to: " + currentUserId);
        loadActiveSubscriptions(); // Reload subscriptions for this user
    }

    
    private void loadActiveSubscriptions() {
        subscriptions.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT SubscriptionID, SubscriptionName, Description, Price, DurationInDays " +
                         "FROM Subscriptions WHERE UserID = ?")) {

            statement.setInt(1, currentUserId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                subscriptions.add(new Subscription(
                    resultSet.getInt("SubscriptionID"),
                    resultSet.getString("SubscriptionName"),
                    resultSet.getString("Description"),
                    resultSet.getDouble("Price"),
                    resultSet.getInt("DurationInDays"),
                    currentUserId
                ));
            }
            subscriptionTable.setItems(subscriptions);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML
    private void onSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        searchResults.clear();

        for (Subscription subscription : mockupData) {
            if (subscription.getName().toLowerCase().contains(searchTerm)) {
                searchResults.add(subscription);
            }
        }

        subscriptionTable.setItems(searchResults.isEmpty() ? subscriptions : searchResults);
    }

    @FXML
    private void onAddSubscription() {
        Subscription selected = subscriptionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a subscription to add.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Get the user's current balance
            PreparedStatement balanceStmt = connection.prepareStatement(
                "SELECT AccountBalance FROM User WHERE UserID = ?");
            balanceStmt.setInt(1, currentUserId);
            ResultSet balanceResult = balanceStmt.executeQuery();

            if (balanceResult.next()) {
                double currentBalance = balanceResult.getDouble("AccountBalance");

                // Check if user has sufficient balance
                if (currentBalance < selected.getPrice()) {
                    showAlert("Error", "Insufficient balance to subscribe to " + selected.getName());
                    return;
                }

                // Deduct the subscription price
                double newBalance = currentBalance - selected.getPrice();
                PreparedStatement updateBalanceStmt = connection.prepareStatement(
                    "UPDATE User SET AccountBalance = ? WHERE UserID = ?");
                updateBalanceStmt.setDouble(1, newBalance);
                updateBalanceStmt.setInt(2, currentUserId);
                updateBalanceStmt.executeUpdate();
            } else {
                showAlert("Error", "Failed to retrieve user balance.");
                return;
            }

            // Check if the subscription already exists for the user
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT SubscriptionID, DurationInDays, Price FROM Subscriptions WHERE UserID = ? AND SubscriptionName = ?");
            checkStmt.setInt(1, currentUserId);
            checkStmt.setString(2, selected.getName());
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                // Update the existing subscription
                int currentDuration = resultSet.getInt("DurationInDays");
                double currentPrice = resultSet.getDouble("Price");

                PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Subscriptions SET DurationInDays = ?, Price = ? WHERE SubscriptionID = ?");
                updateStmt.setInt(1, currentDuration + selected.getDurationInDays());
                updateStmt.setDouble(2, currentPrice + selected.getPrice());
                updateStmt.setInt(3, resultSet.getInt("SubscriptionID"));

                updateStmt.executeUpdate();
                showAlert("Success", "Subscription updated successfully.");
            } else {
                // Insert a new subscription
                PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO Subscriptions (SubscriptionName, Description, Price, DurationInDays, UserID) VALUES (?, ?, ?, ?, ?)");
                insertStmt.setString(1, selected.getName());
                insertStmt.setString(2, selected.getDescription());
                insertStmt.setDouble(3, selected.getPrice());
                insertStmt.setInt(4, selected.getDurationInDays());
                insertStmt.setInt(5, currentUserId);

                insertStmt.executeUpdate();
                showAlert("Success", "Subscription added successfully.");
            }

            loadActiveSubscriptions();
        } catch (SQLException e) {
            showAlert("Error", "Failed to add/update subscription: " + e.getMessage());
            e.printStackTrace();
        }
    }





    /**
     * Cancels a subscription for a specific duration or entirely.
     */
    @FXML
    private void onCancelSubscription() {
        Subscription selected = subscriptionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a subscription to cancel.");
            return;
        }

        // Prompt the user for full or partial cancellation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Subscription");
        alert.setHeaderText("Do you want to cancel this subscription?");
        alert.setContentText("Choose your option.");

        ButtonType oneMonth = new ButtonType("Cancel One Month");
        ButtonType allMonths = new ButtonType("Cancel All");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(oneMonth, allMonths, cancel);

        ButtonType result = alert.showAndWait().orElse(cancel);

        if (result == oneMonth) {
            cancelSubscriptionDuration(selected, 1); // Cancel one month (30 days)
        } else if (result == allMonths) {
            cancelSubscriptionDuration(selected, selected.getDurationInDays() / 30); // Cancel all remaining months
        }
    }


    private void cancelSubscriptionDuration(Subscription subscription, int monthsToCancel) {
        int daysToCancel = monthsToCancel * 30; // Calculate the number of days to cancel

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (daysToCancel >= subscription.getDurationInDays()) {
                // Delete the subscription entirely if the duration becomes 0 or less
                PreparedStatement deleteStmt = connection.prepareStatement(
                    "DELETE FROM Subscriptions WHERE SubscriptionID = ? AND UserID = ?");
                deleteStmt.setInt(1, subscription.getSubscriptionID());
                deleteStmt.setInt(2, currentUserId);

                deleteStmt.executeUpdate();
                showAlert("Success", "Subscription canceled entirely.");
            } else {
                // Update the subscription duration
                PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Subscriptions SET DurationInDays = DurationInDays - ? WHERE SubscriptionID = ? AND UserID = ?");
                updateStmt.setInt(1, daysToCancel); // Subtract the correct number of days
                updateStmt.setInt(2, subscription.getSubscriptionID());
                updateStmt.setInt(3, currentUserId);

                updateStmt.executeUpdate();
                showAlert("Success", "Subscription updated successfully.");
            }

            loadActiveSubscriptions();
        } catch (SQLException e) {
            showAlert("Error", "Failed to update subscription: " + e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * Handles back button functionality.
     */
    @FXML
    private void onBack(ActionEvent event) {
        // Ensure the currentUserId is retained
        System.out.println("SubscriptionController: Setting currentUserId in SessionManager before navigating back.");
        SessionManager.getInstance().setCurrentUserId(this.currentUserId);

        // Navigate back to the dashboard
        AppUtils.changeScene(event, "/views/Dashboard.fxml");
    }

    
    




    /**
     * Displays an alert dialog.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

   
}
