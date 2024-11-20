package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Income;

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
    
    
    @FXML
    private TextField userIDField;
    
    
    @FXML
    private void addIncome(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.of(selectedDate, currentTime);
        
        String source = sourceField.getText();
        
        double amount = Double.parseDouble(amountField.getText());
        
        int userID = Integer.parseInt(userIDField.getText());
        
        new Income(amount, source, dateTime, userID);
    }
}
