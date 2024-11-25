package controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    private Label usernameLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private ListView<String> transactionList;

    @FXML
    public void initialize() {
        // Load current user details from SessionManager
        String username = SessionManager.getInstance().getCurrentUserName();
        double balance = SessionManager.getInstance().getCurrentUserBalance();

        // Update labels
        usernameLabel.setText("Username: " + username);
        balanceLabel.setText("Balance: $" + balance);

        // Load recent transactions (placeholder for now)
        transactionList.getItems().addAll("Transaction 1", "Transaction 2", "Transaction 3");
    }
    
    public void openSendMoney(ActionEvent event) {
    	
        AppUtils.changeScene(event, "/views/SendMoney.fxml");
        
    }
    public void openTransactions(ActionEvent event) {

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
		
		SessionManager.getInstance().clearSession();
         
		AppUtils.changeScene(event, "/views/Login.fxml");
    }
    
    public void trackIncome(ActionEvent event) {
        AppUtils.changeScene(event, "/views/TrackIncome.fxml");
    }
    public void trackExpense(ActionEvent event) {
        AppUtils.changeScene(event, "/views/TrackExpense.fxml");
    }

    public void paymentRequest(ActionEvent event) {
        AppUtils.changeScene(event, "/views/PaymentRequest.fxml");
    }

    @FXML
    private void FinancialHealth(ActionEvent event) {
    	AppUtils.changeScene(event, "/views/FinancialHealth.fxml");

    }
    @FXML
    private void Budget(ActionEvent event) {
    	AppUtils.changeScene(event, "/views/TrackBudget.fxml");
    }
    @FXML
    private void taxPayment(ActionEvent event) {
    	AppUtils.changeScene(event, "/views/TaxPayment.fxml");
    }
    @FXML
    private void convertCurrncy(ActionEvent event) {
    	AppUtils.changeScene(event, "/views/ConvertCurrency.fxml");
    }
    @FXML
    private void subscriptions(ActionEvent event) {
    	AppUtils.changeScene(event, "/views/Subscription.fxml");
    }
}
