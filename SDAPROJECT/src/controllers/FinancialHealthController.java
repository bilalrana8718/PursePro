package controllers;

import application.AppUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import models.FinancialHealthScore;

public class FinancialHealthController {
	@FXML Label incomeToExpenseLabel;
	@FXML Label savingsPotentialLabel;
	@FXML Label scoreLabel;
	@FXML Text suggestionsText;
	
	private FinancialHealthScore calculator = FinancialHealthScore.getInstance();
	@FXML
	private void calculateFinancialHealth(ActionEvent event){
		calculator.calculateHealthScore();
		incomeToExpenseLabel.setText(String.format(" %.2f ",calculator.getIncomeToExpenseRatio()*100)+" %");
		savingsPotentialLabel.setText(String.format(" %.2f ",calculator.getSavingPotential())+ " %");		
		scoreLabel.setText(String.format(" %.2f", calculator.getScore()));
		suggestionsText.setText(calculator.getSuggestion());
		
		calculator.insertToDataBase();
	}
	
    @FXML
    private void backToDashBoard(ActionEvent event){
        AppUtils.changeScene(event, "/views/Dashboard.fxml");
    }

}