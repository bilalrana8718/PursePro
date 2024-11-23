package controllers;

import db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.SessionManager;
import models.TransactionHistory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;

public class TransactionHistoryController {

    @FXML
    private TableView<TransactionHistory> transactionTable;

    @FXML
    private TableColumn<TransactionHistory, String> dateColumn;

    @FXML
    private TableColumn<TransactionHistory, Double> amountColumn;

    @FXML
    private TableColumn<TransactionHistory, String> categoryColumn;

    @FXML
    private TableColumn<TransactionHistory, String> recipientColumn;
    
    @FXML
    private TableColumn<TransactionHistory, String> descriptionColumn;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label statusLabel;

    private ObservableList<TransactionHistory> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty().asString());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        recipientColumn.setCellValueFactory(cellData -> cellData.getValue().recipientProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        // Load all transactions for the current user
        loadTransactions(null, null);
    }

    private void loadTransactions(LocalDate startDate, LocalDate endDate) {
        transactionList.clear();

        try {
            ResultSet resultSet = TransactionHistory.getTransactionResultSet(
                SessionManager.getInstance().getCurrentUserId(),
                startDate,
                endDate
            );

            while (resultSet.next()) {
                transactionList.add(new TransactionHistory(
                        resultSet.getInt("TransactionID"),
                        resultSet.getDouble("Amount"),
                        resultSet.getString("Category"),
                        resultSet.getTimestamp("Date").toLocalDateTime(),
                        resultSet.getString("Description"),
                        SessionManager.getInstance().getCurrentUserId(), // Current user ID
                        resultSet.getString("Recipient") // Recipient name
                ));
            }

            transactionTable.setItems(transactionList);

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading transactions.");
        }
    }

    @FXML
    public void filterTransactions() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            statusLabel.setText("Please select both start and end dates.");
            return;
        }

        loadTransactions(startDate, endDate);
        statusLabel.setText("Transactions filtered by date.");
    }

    @FXML
    public void exportToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("TransactionHistory.csv"))) {
            writer.println("Date,Amount,Category,Recipient");

            for (TransactionHistory transaction : transactionList) {
                writer.printf("%s,%.2f,%s,%s%n",
                        transaction.getDate().toString(),
                        transaction.getAmount(),
                        transaction.getCategory(),
                        transaction.getRecipient());
            }

            statusLabel.setText("Transaction history exported successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error exporting transaction history.");
        }
    }
}