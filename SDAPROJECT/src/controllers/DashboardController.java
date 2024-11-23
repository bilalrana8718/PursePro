package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.SessionManager;

import java.io.IOException;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import application.AppUtils;

public class DashboardController {

    @FXML
    private ListView<String> transactionList;

    @FXML
    
    
    public void initialize() {
        // Populate the transaction list with sample data
        transactionList.getItems().addAll(
            "Received $200 from John",
            "Paid $50 for groceries",
            "Received $150 from PayPal",
            "Paid $20 for coffee"
        );
    }

 public void openSendMoney(ActionEvent event) {
    	
        AppUtils.changeScene(event, "/views/SendMoney.fxml");
        
    }

    public void openTransactions(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction History");
        alert.setHeaderText(null);
        alert.setContentText("View all transactions here!");
        alert.showAndWait();
        AppUtils.changeScene(event, "/views/TransactionHistory.fxml");
    }


    public void openSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText(null);
        alert.setContentText("Access application settings here.");
        alert.showAndWait();
    }

    public void openAboutUs() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Us");
        alert.setHeaderText(null);
        alert.setContentText("Purse Pro: Your trusted digital wallet app.");
        alert.showAndWait();
    }

    public void logout(ActionEvent event) {
        try {
            // Show a confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Logout");
            alert.setHeaderText(null);
            alert.setContentText("You have been logged out.");
            alert.showAndWait();

            // Load the Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage (window) from the event
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene to the stage
            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int currentUserId;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    
    
    //Addition
    @FXML
    private void onSubscription(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Subscription.fxml"));
            Parent root = loader.load();

            // Set the current user ID in SessionManager
            SessionManager.getInstance().setCurrentUserId(this.currentUserId);
            System.out.println("DashboardController: Set currentUserId in SessionManager = " + this.currentUserId);

            // Navigate to the Subscription page
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to load Subscription page: " + e.getMessage());
        }
    }

    
    //Addition
    @FXML
    private void openConvertCurrency(ActionEvent event) {
        AppUtils.changeScene(event, "/views/ConvertCurrency.fxml");
    }

    
    public void trackIncome(ActionEvent event) {
        AppUtils.changeScene(event, "/views/TrackIncome.fxml");
    }
    public void trackExpense(ActionEvent event) {
        AppUtils.changeScene(event, "/views/TrackExpense.fxml");
    }
    @FXML
    private void FinancialHealth(ActionEvent event) {
    	AppUtils.changeScene(event, "/views/FinancialHealth.fxml");
    }


}
