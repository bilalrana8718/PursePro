package models;

import java.sql.*;

import db.DatabaseConnection;
import javafx.beans.property.*;
import javafx.collections.*;
public class TaxPayment {
    private final SimpleStringProperty startDate;
    private final SimpleStringProperty endDate;
    private final SimpleDoubleProperty taxPaid;
    private final SimpleStringProperty paymentDate;

    public TaxPayment(String startDate, String endDate, double taxPaid, String paymentDate) {
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
        this.taxPaid = new SimpleDoubleProperty(taxPaid);
        this.paymentDate = new SimpleStringProperty(paymentDate);
    }

    public String getStartDate() {
        return startDate.get();
    }

    public void setStartDate(String startDate) {
        this.startDate.set(startDate);
    }

    public String getEndDate() {
        return endDate.get();
    }

    public void setEndDate(String endDate) {
        this.endDate.set(endDate);
    }

    public double getTaxPaid() {
        return taxPaid.get();
    }

    public void setTaxPaid(double taxPaid) {
        this.taxPaid.set(taxPaid);
    }

    public String getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate.set(paymentDate);
    }
    
    public static double calculateTax(double totalIncome, double deductibleExpenses) {
        double taxableIncome = totalIncome - deductibleExpenses;
        if (taxableIncome <= 0) return 0;

        // Example tax brackets
        if (taxableIncome <= 5000) return taxableIncome * 0.1;  // 10%
        if (taxableIncome <= 20000) return 500 + (taxableIncome - 5000) * 0.2; // 20%
        return 3500 + (taxableIncome - 20000) * 0.3; // 30%
    }
    
    public static ObservableList<TaxPayment> fetchTaxPaymentData() throws SQLException {
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
        }

        return taxData;
    }
}
