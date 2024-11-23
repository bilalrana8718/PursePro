package controllers;

import db.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Transaction;
import models.Expense;
import models.SessionManager;
import application.AppUtils;
import java.io.IOException;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SendMoneyController {

    @FXML
    private TextField recipientAccountField;

    @FXML
    private TextField amountField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> categoryField;

    @FXML
    private Label totalLabel;

    private double totalAmount = 0.0;

    @FXML
    public void initialize() {
        // Load categories into ComboBox
        categoryField.getItems().addAll("Groceries", "Rent", "Utilities", "Travel", "Miscellaneous");
    }

    @FXML
    public void calculateTotal() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            double fee = 0.02 * amount; // Assume a 2% fee
            totalAmount = amount + fee;
            totalLabel.setText(String.format("Total Amount: $%.2f", totalAmount));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount.");
        }
    }
    

    @FXML
    public void sendMoney(ActionEvent event) {
        String recipientEmail = recipientAccountField.getText();
        String category = categoryField.getValue();
        String description = descriptionField.getText();

        if (recipientEmail.isEmpty() || amountField.getText().isEmpty() || category == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());

            // Check if recipient exists
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement checkRecipient = connection.prepareStatement("SELECT UserID FROM User WHERE Username = ?");
            checkRecipient.setString(1, recipientEmail);
            ResultSet recipientResult = checkRecipient.executeQuery();

            if (!recipientResult.next()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Recipient", "The recipient does not exist.");
                return;
            }

            int recipientID = recipientResult.getInt("UserID");

            // Check sender's balance
            PreparedStatement checkBalance = connection.prepareStatement("SELECT AccountBalance FROM User WHERE UserID = ?");
            checkBalance.setInt(1, getCurrentUserID());
            ResultSet balanceResult = checkBalance.executeQuery();

            if (balanceResult.next()) {
                double senderBalance = balanceResult.getDouble("AccountBalance");
                if (senderBalance < totalAmount) {
                    showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "You do not have enough balance for this transaction.");
                    return;
                }
                
                //add expense
                new Expense(amount, category).insertToDataBase();
                
                // Deduct amount from sender
                PreparedStatement deductAmount = connection.prepareStatement("UPDATE User SET AccountBalance = AccountBalance - ? WHERE UserID = ?");
                deductAmount.setDouble(1, totalAmount);
                deductAmount.setInt(2, getCurrentUserID());
                deductAmount.executeUpdate();

                // Add amount to recipient
                PreparedStatement addAmount = connection.prepareStatement("UPDATE User SET AccountBalance = AccountBalance + ? WHERE UserID = ?");
                addAmount.setDouble(1, amount);
                addAmount.setInt(2, recipientID);
                addAmount.executeUpdate();
                
                Transaction tran = new Transaction(amount, category, description, getCurrentUserID(), recipientID);
                tran.addTransaction();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Money sent successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while processing the transaction.");
        }
    }
    
    @FXML
    public void goBack(ActionEvent event) {
        AppUtils.changeScene(event, "/views/Dashboard.fxml");
    }

    private int getCurrentUserID() {
    	
        return SessionManager.getInstance().getCurrentUserId(); // Replace with actual logic
    }
    
    

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    
}
