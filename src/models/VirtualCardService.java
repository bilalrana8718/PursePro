package models;

import db.DatabaseConnection;
import models.VirtualCard;

import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class VirtualCardService {

    public boolean hasVirtualCard(int userId) {
        String query = "SELECT COUNT(*) AS count FROM VIRTUAL_CARDS WHERE UserID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt("count") > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean issueVirtualCard(int userId, double cardLimit, double issuanceFee) {
        if (hasVirtualCard(userId)) return false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check user balance
            String balanceQuery = "SELECT AccountBalance FROM User WHERE UserID = ?";
            PreparedStatement balanceStmt = connection.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, userId);
            ResultSet balanceResult = balanceStmt.executeQuery();

            if (balanceResult.next()) {
                double currentBalance = balanceResult.getDouble("AccountBalance");
                if (currentBalance < issuanceFee) return false;

                // Generate virtual card details
                String cardNumber = generateCardNumber();
                String cvv = generateCVV();
                LocalDate expirationDate = LocalDate.now().plusYears(3);

                // Deduct issuance fee
                String updateBalanceQuery = "UPDATE User SET AccountBalance = AccountBalance - ? WHERE UserID = ?";
                PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
                updateBalanceStmt.setDouble(1, issuanceFee);
                updateBalanceStmt.setInt(2, userId);
                updateBalanceStmt.executeUpdate();

                // Insert new virtual card
                String insertCardQuery = "INSERT INTO VIRTUAL_CARDS (UserID, CardNumber, ExpirationDate, CVV, CardLimit, CardStatus) " +
                                          "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertCardStmt = connection.prepareStatement(insertCardQuery);
                insertCardStmt.setInt(1, userId);
                insertCardStmt.setString(2, cardNumber);
                insertCardStmt.setDate(3, Date.valueOf(expirationDate));
                insertCardStmt.setString(4, cvv);
                insertCardStmt.setDouble(5, cardLimit);
                insertCardStmt.setString(6, "Active");
                insertCardStmt.executeUpdate();

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public VirtualCard getUserCard(int userId) {
        String query = "SELECT * FROM VIRTUAL_CARDS WHERE UserID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new VirtualCard(
                        resultSet.getInt("CardID"),
                        resultSet.getInt("UserID"),
                        resultSet.getString("CardNumber"),
                        resultSet.getDate("ExpirationDate").toLocalDate(),
                        resultSet.getString("CVV"),
                        resultSet.getDouble("CardLimit"),
                        resultSet.getString("CardStatus"),
                        resultSet.getTimestamp("IssuedDate").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean blockUserCard(int userId) {
        String query = "UPDATE VIRTUAL_CARDS SET CardStatus = 'Blocked' WHERE UserID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generateCVV() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}
