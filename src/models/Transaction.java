package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import db.DatabaseConnection;

public class Transaction {

    private int transactionID;
    private double amount;
    private String category;
    private LocalDateTime date;
    private String description;
    private int userID;
    private int recipientID;

    public Transaction(double amount, String category, String description, int userID, int recipientID) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.userID = userID;
        this.recipientID = recipientID;
        this.date = LocalDateTime.now();
    }

    // Getters and setters
    public int getTransactionID() { return transactionID; }
    public void setTransactionID(int transactionID) { this.transactionID = transactionID; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getRecipientID() { return recipientID; }
    public void setRecipientID(int recipientID) { this.recipientID = recipientID; }
    
    public void addTransaction() throws SQLException
    {
        // Record transaction
    	Connection connection = DatabaseConnection.getConnection();
        PreparedStatement recordTransaction = connection.prepareStatement(
                "INSERT INTO Transactions (Amount, Category, Description, UserID, RecipientID) VALUES (?, ?, ?, ?, ?)");
        recordTransaction.setDouble(1, amount);
        recordTransaction.setString(2, category);
        recordTransaction.setString(3, description);
        recordTransaction.setInt(4, userID);
        recordTransaction.setInt(5, recipientID);
        recordTransaction.executeUpdate();
    }
}
