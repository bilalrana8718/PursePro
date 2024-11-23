package controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ReceiptController {

    @FXML
    private Label amountLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label recipientLabel;

    @FXML
    private Label dateLabel;

    public void setReceiptDetails(double amount, String category, String description, String recipient, String date) {
        amountLabel.setText(String.format("Amount: $%.2f", amount));
        categoryLabel.setText("Category: " + category);
        descriptionLabel.setText("Description: " + description);
        recipientLabel.setText("Recipient: " + recipient);
        dateLabel.setText("Date: " + date);
    }

    @FXML
    public void closeModal() {
        Stage stage = (Stage) amountLabel.getScene().getWindow();
        stage.close();
    }
}