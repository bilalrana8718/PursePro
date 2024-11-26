package controllers;

import application.AppUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import models.SessionManager;
import models.VirtualCard;
import models.VirtualCardService;

public class VirtualCardController {

    @FXML
    private Pane cardDetailsPane;

    @FXML
    private Label cardStatusLabel, cardNumberLabel, cardExpiryLabel, cardCVVLabel, cardLimitLabel;

    @FXML
    private Button issueCardButton;

    private VirtualCardService cardService = new VirtualCardService();

    @FXML
    public void initialize() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        VirtualCard card = cardService.getUserCard(userId);

        if (card != null) {
            showCardDetails(card);
        } else {
            cardStatusLabel.setText("You do not have a virtual card.");
            cardDetailsPane.setVisible(false); // Hide card details pane initially
            issueCardButton.setDisable(false); // Enable issuing a new card
        }
    }

    @FXML
    private void issueCardAction() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        double issuanceFee = 5.00; // Set issuance fee
        double cardLimit = 5000.00; // Default card limit

        boolean success = cardService.issueVirtualCard(userId, cardLimit, issuanceFee);

        if (success) {
            VirtualCard card = cardService.getUserCard(userId);
            showCardDetails(card);
        } else {
            cardStatusLabel.setText("Unable to issue card. Check your balance or contact support.");
        }
    }

    @FXML
    private void blockCardAction() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        boolean success = cardService.blockUserCard(userId);

        if (success) {
            cardStatusLabel.setText("Your card has been blocked.");
            clearCardDetails();
        } else {
            cardStatusLabel.setText("Unable to block card. Try again later.");
        }
    }

    private void showCardDetails(VirtualCard card) {
        cardDetailsPane.setVisible(true);
        cardStatusLabel.setText("Your Virtual Card:");
        cardNumberLabel.setText("Card Number: " + card.getCardNumber());
        cardExpiryLabel.setText("Expiration Date: " + card.getExpirationDate());
        cardCVVLabel.setText("CVV: " + card.getCvv());
        cardLimitLabel.setText("Card Limit: $" + card.getCardLimit());
        issueCardButton.setDisable(true); // Disable the issue button if the card exists
    }

    private void clearCardDetails() {
        cardNumberLabel.setText("");
        cardExpiryLabel.setText("");
        cardCVVLabel.setText("");
        cardLimitLabel.setText("");
        cardDetailsPane.setVisible(false);
        issueCardButton.setDisable(false); // Allow issuing a new card
    }
    
    @FXML
    private void goBackAction(ActionEvent event) {
        // Change the scene to the Dashboard.fxml
        AppUtils.changeScene(event, "/views/Dashboard.fxml");
    }
}
