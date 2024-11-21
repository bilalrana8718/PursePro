package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Income;

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
}
