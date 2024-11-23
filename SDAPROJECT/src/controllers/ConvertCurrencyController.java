package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import application.AppUtils;
import models.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class ConvertCurrencyController {

    @FXML
    private TextField accountBalanceField;

    @FXML
    private ChoiceBox<String> currencyChoiceBox;

    @FXML
    private TextField convertedBalanceField;

    private final Map<String, Double> conversionRates = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialize conversion rates
        conversionRates.put("USD", 1.000000);
        conversionRates.put("EUR", 0.920000);
        conversionRates.put("PKR", 275.000000);
        conversionRates.put("GBP", 0.770000);

        // Populate ChoiceBox
        currencyChoiceBox.getItems().addAll("USD", "EUR", "PKR", "GBP");

        // Retrieve account balance from SessionManager or database
        double accountBalance = getAccountBalance();
        accountBalanceField.setText(String.format("%.2f", accountBalance));
    }

    private double getAccountBalance() {
    	
    	    return SessionManager.getInstance().getCurrentUserBalance();
    	

    }

    @FXML
    private void convertCurrency(ActionEvent event) {
        String selectedCurrency = currencyChoiceBox.getValue();
        if (selectedCurrency == null) {
            convertedBalanceField.setText("Select a currency");
            return;
        }

        double balance = Double.parseDouble(accountBalanceField.getText());
        double rate = conversionRates.get(selectedCurrency);
        double convertedBalance = balance * rate;

        convertedBalanceField.setText(String.format("%.2f %s", convertedBalance, selectedCurrency));
    }

    @FXML
    private void goBack(ActionEvent event) {
        AppUtils.changeScene(event, "/views/Dashboard.fxml");
    }
}
