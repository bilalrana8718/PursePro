package controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import models.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Income;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    	try {	
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TrackIncome.fxml"));
    		Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    		stage.setTitle("Add Income");
    		stage.setScene(new Scene(root));
    		stage.show();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    @FXML
    public void checkIfNum(KeyEvent event) {
    	amountField.setText(preAmountField);
    	String c = event.getText();
    	if(c.substring(c.length()-1).compareTo("1") >= 0 && c.substring(c.length()-1).compareTo("9") <= 0) {
    		preAmountField += c.substring(c.length()-1);
    	}
    	amountField.setText(preAmountField);
    }
}
