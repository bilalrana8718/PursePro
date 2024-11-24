

package models;

import javafx.beans.property.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import db.DatabaseConnection;

public class TransactionHistory {

    private final IntegerProperty transactionID;
    private final DoubleProperty amount;
    private final StringProperty category;
    private final ObjectProperty<LocalDateTime> date;
    private final StringProperty description;
    private final IntegerProperty userID;
    private final StringProperty recipient; // Store recipient name for display purposes

    public TransactionHistory(int transactionID, double amount, String category, LocalDateTime date, String description, int userID, String recipient) {
        this.transactionID = new SimpleIntegerProperty(transactionID);
        this.amount = new SimpleDoubleProperty(amount);
        this.category = new SimpleStringProperty(category);
        this.date = new SimpleObjectProperty<>(date);
        this.description = new SimpleStringProperty(description);
        this.userID = new SimpleIntegerProperty(userID);
        this.recipient = new SimpleStringProperty(recipient);
    }


    // Getters and setters for properties
    public int getTransactionID() { return transactionID.get(); }
    public IntegerProperty transactionIDProperty() { return transactionID; }
    public void setTransactionID(int transactionID) { this.transactionID.set(transactionID); }

    public double getAmount() { return amount.get(); }
    public DoubleProperty amountProperty() { return amount; }
    public void setAmount(double amount) { this.amount.set(amount); }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }
    public void setCategory(String category) { this.category.set(category); }

    public LocalDateTime getDate() { return date.get(); }
    public ObjectProperty<LocalDateTime> dateProperty() { return date; }
    public void setDate(LocalDateTime date) { this.date.set(date); }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String description) { this.description.set(description); }

    public int getUserID() { return userID.get(); }
    public IntegerProperty userIDProperty() { return userID; }
    public void setUserID(int userID) { this.userID.set(userID); }

    public String getRecipient() { return recipient.get(); }
    public StringProperty recipientProperty() { return recipient; }
    public void setRecipient(String recipient) { this.recipient.set(recipient); }
    
    
//    public static ResultSet getTransactionResultSet(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
//        Connection connection = DatabaseConnection.getConnection();
//        String query = "SELECT t.TransactionID, t.Amount, t.Category, t.Date, t.Description, u.UserName AS Recipient " +
//                       "FROM Transactions t " +
//                       "JOIN User u ON t.RecipientID = u.UserID " +
//                       "WHERE t.UserID = ?";
//
//        // Add date filtering if provided
//        if (startDate != null && endDate != null) {
//            query += " AND t.Date BETWEEN ? AND ?";
//        }
//
//        PreparedStatement statement = connection.prepareStatement(query);
//        statement.setInt(1, userId);
//
//        if (startDate != null && endDate != null) {
//            statement.setDate(2, Date.valueOf(startDate));
//            statement.setDate(3, Date.valueOf(endDate));
//        }
//
//        return statement.executeQuery();
//    }
    
    public static ResultSet getTransactionResultSet(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT t.TransactionID, t.Amount, t.Category, t.Date, t.Description, " +
                       "CASE WHEN t.UserID = ? THEN u.UserName ELSE s.UserName END AS Recipient " +
                       "FROM Transactions t " +
                       "LEFT JOIN User u ON t.RecipientID = u.UserID " +
                       "LEFT JOIN User s ON t.UserID = s.UserID " +
                       "WHERE t.UserID = ? OR t.RecipientID = ?";

        if (startDate != null && endDate != null) {
            query += " AND t.Date BETWEEN ? AND ?";
        }

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        statement.setInt(2, userId);
        statement.setInt(3, userId);

        if (startDate != null && endDate != null) {
            statement.setDate(4, Date.valueOf(startDate));
            statement.setDate(5, Date.valueOf(endDate));
        }

        return statement.executeQuery();
    }

}
