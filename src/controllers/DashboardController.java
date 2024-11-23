package controllers;

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
import models.SessionManager;

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

    public void logout(ActionEvent event) throws IOException {
        // Show a confirmation message
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Logout");
		alert.setHeaderText(null);
		alert.setContentText("You have been logged out.");
		alert.showAndWait();
		
		SessionManager.getInstance().clearSession();
         
		AppUtils.changeScene(event, "/views/Login.fxml");
    }
    
    public void trackIncome(ActionEvent event) {
    	System.out.println("hhhh");
    	try {
            // Show a confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Track Income");
            alert.setHeaderText(null);

            // Load the Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TrackIncome.fxml"));
            Parent incomeRoot = loader.load();

            // Get the current stage (window) from the event
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene to the stage
            Scene scene = new Scene(incomeRoot);
            stage.setScene(scene);
            stage.setTitle("Track income");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            
        }    	
    }

}
