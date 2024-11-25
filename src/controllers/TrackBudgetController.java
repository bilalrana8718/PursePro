package controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import application.AppUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import models.Budget;
import models.BudgetManager;

public class TrackBudgetController {
	private String preAmountField = "";
	@FXML private TableView<Budget> budgetTable;
	@FXML private TableColumn<Budget, String> categoryColumn;
	@FXML private TableColumn<Budget, Double> amountColumn;
	@FXML private TableColumn<Budget, String> dateColumn;
	@FXML private ComboBox<String> categoryField;
	@FXML private TextField amountField;
	
	private BudgetManager budMan = BudgetManager.getInstance();

	private ObservableList<Budget> budgetList = FXCollections.observableArrayList();

	
    @FXML
    private void initialize() {
        // Initialize the columns
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
//        dateColumn.setCellValueFactory(cellData -> (cellData.getValue().dateProperty()).asString());

        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDateTime();
            String formattedDateTime = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm:ss")) : "";
            return new SimpleStringProperty(formattedDateTime);
        });
        
        
        categoryField.setItems(FXCollections.observableArrayList("Groceries", "Rent", "Utilities", "Travel", "Miscellaneous"));
        
   
        budgetList = budMan.getBudgetList(budgetList);
        budgetTable.setItems(budgetList); 
    }
	
	@FXML
	private void backToDashBoard(ActionEvent event) {
		AppUtils.changeScene(event, "/views/Dashboard.fxml");
	}
	@FXML
	private void updateBudget(ActionEvent event) {
    	if(categoryField.getValue() == null || preAmountField.isBlank())
    	{
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");  
            return;
    	}
    	
    	budMan.updateBudget(Double.parseDouble(preAmountField),Double.parseDouble(preAmountField), categoryField.getValue());
        
    	budgetList = budMan.getBudgetList(budgetList);
        budgetTable.setItems(budgetList);     	 	
	}
	
	@FXML
	private void addBudget(ActionEvent event) {
		if(categoryField.getValue() == null || preAmountField.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");  
            return;			
		}
		
		budMan.addBudget(Double.parseDouble(preAmountField),Double.parseDouble(preAmountField), categoryField.getValue());
		
		budgetList = budMan.getBudgetList(budgetList);
		budgetTable.setItems(budgetList);
	}
	@FXML
	private void removeBudget(ActionEvent event) {
		if(categoryField.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill Category fields.");  
            return;			
		}
		budMan.removeBudget(categoryField.getValue());
		
		budgetList = budMan.getBudgetList(budgetList);
		budgetTable.setItems(budgetList);
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
