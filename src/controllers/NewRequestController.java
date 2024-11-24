package controllers;

import db.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import models.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.AppUtils;

public class NewRequestController {

    @FXML
    private TextField recipientEmailField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea descriptionField;
    
    @FXML
    private ComboBox<String> categoryField;
    
    @FXML
    public void initialize() {
        // Load categories into ComboBox
        categoryField.getItems().addAll("Groceries", "Rent", "Utilities", "Travel", "Miscellaneous");
    }
    
    @FXML
    private void submitRequest() {
        String recipientEmail = recipientEmailField.getText();
        String amountText = amountField.getText();
        String description = descriptionField.getText();
        String category = categoryField.getValue();

        if (recipientEmail.isEmpty() || amountText.isEmpty() || category.isEmpty()) {
            showAlert(AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                throw new NumberFormatException("Amount must be positive.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Validation Error", "Please enter a valid amount.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Validate recipient
            String recipientQuery = "SELECT UserID FROM User WHERE Username = ?";
            PreparedStatement recipientStatement = connection.prepareStatement(recipientQuery);
            recipientStatement.setString(1, recipientEmail);
            ResultSet recipientResult = recipientStatement.executeQuery();

            if (!recipientResult.next()) {
                showAlert(AlertType.ERROR, "Validation Error", "Recipient not found.");
                return;
            }

            int recipientId = recipientResult.getInt("UserID");
            int senderId = SessionManager.getInstance().getCurrentUserId();

            if (recipientId == senderId) {
                showAlert(AlertType.ERROR, "Validation Error", "You cannot send a request to yourself.");
                return;
            }

            // Insert payment request
            String insertQuery = "INSERT INTO PaymentRequest (SenderID, RecipientID, Category, Amount, Status) VALUES (?, ?, ?, ?, 'Pending')";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, senderId);
            insertStatement.setInt(2, recipientId);
            insertStatement.setString(3, category);
            insertStatement.setDouble(4, amount);
            insertStatement.executeUpdate();

            showAlert(AlertType.INFORMATION, "Success", "Payment request sent successfully.");

            // Clear fields
            recipientEmailField.clear();
            amountField.clear();
            descriptionField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to send payment request: " + e.getMessage());
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        // Logic to go back to the Payment Requests page
       AppUtils.changeScene(event, "/views/PaymentRequest.fxml");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
