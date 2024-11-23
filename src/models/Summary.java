package models;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import javafx.collections.ObservableList;

public class Summary {

    /**
     * Exports the transaction history to a CSV file.
     * 
     * @param transactionList The list of transactions to export.
     * @param filePath The file path where the CSV file will be saved.
     * @return A message indicating success or failure.
     */
    public static String exportToCSV(ObservableList<TransactionHistory> transactionList, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write the CSV header
            writer.println("Date,Amount,Category,Recipient");

            // Write each transaction's details
            for (TransactionHistory transaction : transactionList) {
                writer.printf("%s,%.2f,%s,%s%n",
                        transaction.getDate().toString(),   // Convert LocalDateTime to String
                        transaction.getAmount(),           // Transaction amount
                        transaction.getCategory(),         // Transaction category
                        transaction.getRecipient());       // Recipient name
            }

            // Return success message
            return "Transaction history exported successfully to " + filePath;

        } catch (Exception e) {
            e.printStackTrace();
            // Return error message
            return "Error exporting transaction history: " + e.getMessage();
        }
    }
}
