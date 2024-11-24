package controllers;

import db.DatabaseConnection;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.IncomeManager;
import models.ExpenseManager;
import models.SessionManager;
import models.TaxPayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaxCalculatorController {
	
	@FXML
	private TableView<TaxPayment> taxPaymentsTable;
	@FXML
	private TableColumn<TaxPayment, String> startDateColumn;
	@FXML
	private TableColumn<TaxPayment, String> endDateColumn;
	@FXML
	private TableColumn<TaxPayment, Double> taxPaidColumn;
	@FXML
	private TableColumn<TaxPayment, String> paymentDateColumn;
    @FXML
    private Label incomeLabel, expenseLabel, deductibleLabel, taxEstimateLabel, statusLabel;

    @FXML
    private Button calculateTaxButton, payTaxButton, generateReportButton;

    @FXML
    private DatePicker taxYearStartPicker, taxYearEndPicker;

    private IncomeManager incomeManager = IncomeManager.getInstance();
    private ExpenseManager expenseManager = ExpenseManager.getInstance();

    @FXML
    public void initialize() {
        // Initialize tax year with the current year's range
        LocalDate today = LocalDate.now();
        taxYearStartPicker.setValue(today.withDayOfYear(1));
        taxYearEndPicker.setValue(today);
        updateSummary();
        
        // Configure the table columns
        startDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate()));
        taxPaidColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTaxPaid()).asObject());
        paymentDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaymentDate()));

    }

    private void updateSummary() {
        try {
            LocalDate startDate = taxYearStartPicker.getValue();
            LocalDate endDate = taxYearEndPicker.getValue();

            if (startDate == null || endDate == null) {
                statusLabel.setText("Please select a valid tax year range.");
                return;
            }

            // Retrieve total income and expenses from managers
            double totalIncome = incomeManager.getTotalIncome(startDate.atStartOfDay(), endDate.atStartOfDay(), "");
            double totalExpenses = expenseManager.getTotalExpense(startDate.atStartOfDay(), endDate.atStartOfDay(), "all");

            // Update UI elements with calculated values
            incomeLabel.setText(String.format("$%.2f", totalIncome));
            expenseLabel.setText(String.format("$%.2f", totalExpenses));

            double deductibleExpenses = Math.min(totalExpenses, totalIncome * 0.5); // 50% cap on deductions
            deductibleLabel.setText(String.format("$%.2f", deductibleExpenses));

            double taxEstimate = calculateTax(totalIncome, deductibleExpenses);
            taxEstimateLabel.setText(String.format("$%.2f", taxEstimate));

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error calculating summary. Please try again.");
        }
    }

    private double calculateTax(double totalIncome, double deductibleExpenses) {
        double taxableIncome = totalIncome - deductibleExpenses;
        if (taxableIncome <= 0) return 0;

        // Example tax brackets
        if (taxableIncome <= 5000) return taxableIncome * 0.1;  // 10%
        if (taxableIncome <= 20000) return 500 + (taxableIncome - 5000) * 0.2; // 20%
        return 3500 + (taxableIncome - 20000) * 0.3; // 30%
    }

    @FXML
    private void calculateTaxAction() {
        updateSummary();
    }

    @FXML
    private void payTaxAction() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            LocalDate startDate = taxYearStartPicker.getValue();
            LocalDate endDate = taxYearEndPicker.getValue();

            if (startDate == null || endDate == null) {
                statusLabel.setText("Please select a valid tax year range.");
                return;
            }

            double taxAmount = Double.parseDouble(taxEstimateLabel.getText().replace("$", ""));
            int userId = SessionManager.getInstance().getCurrentUserId();

         // Check if a tax payment for the selected date range overlaps with any existing range
            String checkQuery = "SELECT COUNT(*) AS count FROM TaxPayments WHERE UserID = ? AND (StartDate <= ? AND EndDate >= ?)";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, userId);
            checkStmt.setDate(2, java.sql.Date.valueOf(endDate)); // Selected end date
            checkStmt.setDate(3, java.sql.Date.valueOf(startDate)); // Selected start date
            ResultSet checkResult = checkStmt.executeQuery();

            if (checkResult.next() && checkResult.getInt("count") > 0) {
                statusLabel.setText("Tax for this period already paid");
                return;
            }


            // Check account balance
            PreparedStatement balanceStmt = connection.prepareStatement("SELECT AccountBalance FROM User WHERE UserID = ?");
            balanceStmt.setInt(1, userId);
            ResultSet balanceResult = balanceStmt.executeQuery();

            if (balanceResult.next()) {
                double currentBalance = balanceResult.getDouble("AccountBalance");
                if (currentBalance < taxAmount) {
                    statusLabel.setText("Insufficient funds to pay tax.");
                    return;
                }

                // Deduct tax amount
                PreparedStatement deductStmt = connection.prepareStatement("UPDATE User SET AccountBalance = AccountBalance - ? WHERE UserID = ?");
                deductStmt.setDouble(1, taxAmount);
                deductStmt.setInt(2, userId);
                deductStmt.executeUpdate();

                // Record tax payment
                PreparedStatement taxPaymentStmt = connection.prepareStatement(
                        "INSERT INTO TaxPayments (UserID, StartDate, EndDate, TaxPaid) VALUES (?, ?, ?, ?)"
                );
                taxPaymentStmt.setInt(1, userId);
                taxPaymentStmt.setDate(2, java.sql.Date.valueOf(startDate));
                taxPaymentStmt.setDate(3, java.sql.Date.valueOf(endDate));
                taxPaymentStmt.setDouble(4, taxAmount);
                taxPaymentStmt.executeUpdate();

                statusLabel.setText("Tax payment successful.");
                generateReportAction(); // Refresh the payment table
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error processing tax payment.");
        }
    }


    @FXML
    private void generateReportAction() {
        ObservableList<TaxPayment> taxData = FXCollections.observableArrayList();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT StartDate, EndDate, TaxPaid, PaymentDate FROM TaxPayments WHERE UserID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, SessionManager.getInstance().getCurrentUserId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String startDate = resultSet.getDate("StartDate").toString();
                String endDate = resultSet.getDate("EndDate").toString();
                double taxPaid = resultSet.getDouble("TaxPaid");
                String paymentDate = resultSet.getTimestamp("PaymentDate").toLocalDateTime().toString();

                taxData.add(new TaxPayment(startDate, endDate, taxPaid, paymentDate));
            }

            // Populate the TableView
            taxPaymentsTable.setItems(taxData);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error fetching tax payment data.");
            errorAlert.showAndWait();
        }
    }
}
