package controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import models.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Income;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import application.AppUtils;

public class IncomeController {

	@FXML
	private TextField sourceField;
	@FXML
	private TextField amountField;
		
    @FXML
    private DatePicker datePicker;
    
    private String preAmountField = "";
    
    @FXML
    private void addIncome(ActionEvent event) {
		if (datePicker.getValue() == null || sourceField.getText().isBlank() /* || preAmountField.isBlank() */)
    	{
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");  
            return;
    	}
        LocalDate selectedDate = datePicker.getValue();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.of(selectedDate, currentTime);
        
        String source = sourceField.getText();
        
        double amount = Double.parseDouble(preAmountField);
        
        int userID = SessionManager.getInstance().getCurrentUserId();
        
        new Income(amount, source, dateTime, userID).insertToDataBase();

    }
    @FXML
    private void backToTrackIncome(ActionEvent event){
        AppUtils.changeScene(event, "/views/TrackIncome.fxml");
    }
    @FXML
    public void checkIfNum(KeyEvent event) {
    	amountField.setText(preAmountField);
    	KeyCode code = event.getCode();
    	
    	if(code.isDigitKey()) {
    		preAmountField += code.getName();
    	}
    	else if(code == KeyCode.BACK_SPACE) {
    		if(!preAmountField.isEmpty())
    			preAmountField = preAmountField.substring(0, preAmountField.length()-1);
    	}
    	amountField.setText(preAmountField);
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}