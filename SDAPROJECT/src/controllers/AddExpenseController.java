package controllers;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;

import application.AppUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import models.Expense;
import models.Income;
import models.SessionManager;

public class AddExpenseController {
	@FXML
	private ComboBox<String> categoryField;
	@FXML
	private TextField amountField;
		
    @FXML
    private DatePicker datePicker;
    
    private String preAmountField = "";
    
    @FXML
    public void initialize() {
        // Load categories into ComboBox
        categoryField.setItems(FXCollections.observableArrayList("Groceries", "Rent", "Utilities", "Travel", "Miscellaneous"));

    }
    @FXML
    private void addExpense(ActionEvent event) {
    	if(datePicker.getValue() == null || categoryField.getValue() == null || preAmountField.isBlank())
    	{
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");  
            return;
    	}
        LocalDate selectedDate = datePicker.getValue();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.of(selectedDate, currentTime);
        
        String category = categoryField.getValue();
        
        double amount = Double.parseDouble(preAmountField);
        
        
        new Expense(amount,category, dateTime).insertToDataBase();

    }
    @FXML
    private void backToTrackExpense(ActionEvent event){
        AppUtils.changeScene(event, "/views/TrackExpense.fxml");
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