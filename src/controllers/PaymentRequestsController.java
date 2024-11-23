package controllers;

import db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import models.PaymentRequest;
import models.SessionManager;

import java.sql.*;

import application.AppUtils;


public class PaymentRequestsController {

    
    @FXML
    private TableView<PaymentRequest> paymentRequestsTable;

    @FXML
    private TableColumn<PaymentRequest, String> dateCreatedColumn;

    @FXML
    private TableColumn<PaymentRequest, String> senderColumn;

    @FXML
    private TableColumn<PaymentRequest, String> recipientColumn;

    @FXML
    private TableColumn<PaymentRequest, Double> amountColumn;

    @FXML
    private TableColumn<PaymentRequest, String> statusColumn;

    private ObservableList<PaymentRequest> paymentRequestsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        dateCreatedColumn.setCellValueFactory(cellData -> cellData.getValue().dateCreatedProperty().asString());
        senderColumn.setCellValueFactory(cellData -> cellData.getValue().senderNameProperty());
        recipientColumn.setCellValueFactory(cellData -> cellData.getValue().recipientNameProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Load all payment requests for the current user
        loadPaymentRequests();
    }

    private void loadPaymentRequests() {
        paymentRequestsList.clear();

        try (Connection connection = DatabaseConnection.getConnection()) {
        	String query = "SELECT pr.RequestID, pr.Amount, pr.Status, pr.DateCreated, s.UserName AS Sender " +
                    "FROM PaymentRequest pr " +
                    "JOIN User s ON pr.SenderID = s.UserID " +
                    "WHERE pr.RecipientID = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            int userId = SessionManager.getInstance().getCurrentUserId();
            statement.setInt(1, userId);
            

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                paymentRequestsList.add(new PaymentRequest(
                        resultSet.getInt("RequestID"),
                        resultSet.getDouble("Amount"),
                        resultSet.getString("Status"),
                        resultSet.getTimestamp("DateCreated").toLocalDateTime(),
                        resultSet.getString("Sender"),
                        SessionManager.getInstance().getCurrentUserName()
                ));
            }

            paymentRequestsTable.setItems(paymentRequestsList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createNewRequest(ActionEvent event) {
        // Logic to open the new payment request creation UI
    	 AppUtils.changeScene(event, "/views/NewReqeust.fxml");
    }

    @FXML
    private void paySelectedRequest() {
        PaymentRequest selectedRequest = paymentRequestsTable.getSelectionModel().getSelectedItem();

        if (selectedRequest == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a payment request to pay.");
            alert.showAndWait();
            return;
        }

        // Confirm payment
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to pay this request?");
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Payment Request Details");
        confirmationAlert.setContentText(
                "Recipient: " + selectedRequest.getSenderName() + "\n" +
                "Amount: $" + selectedRequest.getAmount() + "\n" +
                "Description: " + selectedRequest.getRecipientName());
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                processPayment(selectedRequest);
            }
        });
    }

    private void processPayment(PaymentRequest selectedRequest) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if the recipient (Sender of the request) exists and fetch their UserID
            String findUserIdQuery = "SELECT UserID FROM User WHERE UserName = ?";
            PreparedStatement findUserIdStmt = connection.prepareStatement(findUserIdQuery);
            findUserIdStmt.setString(1, selectedRequest.getSenderName());
            ResultSet resultSet = findUserIdStmt.executeQuery();

            int recipientUserId = 0;
            if (resultSet.next()) {
                recipientUserId = resultSet.getInt("UserID");
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Recipient", "The sender of the payment request does not exist.");
                return;
            }

            // Check the current user's balance
            String checkBalanceQuery = "SELECT AccountBalance FROM User WHERE UserID = ?";
            PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
            checkBalanceStmt.setInt(1, SessionManager.getInstance().getCurrentUserId());
            ResultSet balanceResult = checkBalanceStmt.executeQuery();

            if (balanceResult.next()) {
                double senderBalance = balanceResult.getDouble("AccountBalance");

                // Validate if the user has enough balance
                if (senderBalance < selectedRequest.getAmount()) {
                    showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "You do not have enough balance to process this payment.");
                    return;
                }

                // Deduct amount from the current user's balance
                String deductQuery = "UPDATE User SET AccountBalance = AccountBalance - ? WHERE UserID = ?";
                PreparedStatement deductStmt = connection.prepareStatement(deductQuery);
                deductStmt.setDouble(1, selectedRequest.getAmount());
                deductStmt.setInt(2, SessionManager.getInstance().getCurrentUserId());
                deductStmt.executeUpdate();

                // Add amount to the recipient's balance
                String addQuery = "UPDATE User SET AccountBalance = AccountBalance + ? WHERE UserID = ?";
                PreparedStatement addStmt = connection.prepareStatement(addQuery);
                addStmt.setDouble(1, selectedRequest.getAmount());
                addStmt.setInt(2, recipientUserId);
                addStmt.executeUpdate();

                // Update the payment request status to 'Completed'
                String updateRequestQuery = "UPDATE PaymentRequest SET Status = 'Completed' WHERE RequestID = ?";
                PreparedStatement updateRequestStmt = connection.prepareStatement(updateRequestQuery);
                updateRequestStmt.setInt(1, selectedRequest.getRequestId());
                updateRequestStmt.executeUpdate();

                // Record the transaction
                String recordTransactionQuery = "INSERT INTO Transactions (Amount, Category, Description, UserID, RecipientID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement recordTransactionStmt = connection.prepareStatement(recordTransactionQuery);
                recordTransactionStmt.setDouble(1, selectedRequest.getAmount());
                recordTransactionStmt.setString(2, "Payment Request");
                recordTransactionStmt.setString(3, "Payment for request ID: " + selectedRequest.getRequestId());
                recordTransactionStmt.setInt(4, SessionManager.getInstance().getCurrentUserId());
                recordTransactionStmt.setInt(5, recipientUserId);
                recordTransactionStmt.executeUpdate();

                // Show success alert
                showAlert(Alert.AlertType.INFORMATION, "Payment Success", "The payment was completed successfully.");

                // Refresh the payment requests table
                loadPaymentRequests();

            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to retrieve your account balance.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Payment Error", "An error occurred while processing the payment: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Optional: You can add a header here if needed
        alert.setContentText(message);
        alert.showAndWait();
    }



    
    

}
