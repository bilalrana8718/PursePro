package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.DatePicker;
import models.Income; // Model class for Income (you need to create this class)
import models.IncomeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import application.AppUtils;
import javafx.event.ActionEvent;

public class TrackIncomeController {
	private IncomeManager incMan = IncomeManager.getInstance();
    @FXML
	private TableView<Income> incomesTable;
    @FXML private TableColumn<Income, String> sourceColumn;
    @FXML private TableColumn<Income, Double> amountColumn;
    @FXML private TableColumn<Income, String> dateColumn;
    @FXML private Label totalIncomeLabel;
    @FXML private Button addIncomeButton;
    @FXML private TextField sourceField;
    @FXML private DatePicker startDateField;
    @FXML private DatePicker endDateField;

    private ObservableList<Income> incomeList = FXCollections.observableArrayList();

    @FXML
    public void navigateToAddIncome(ActionEvent event) {
        AppUtils.changeScene(event, "/views/Income.fxml");
    }
    @FXML
    private void backToDashBoard(ActionEvent event){
        AppUtils.changeScene(event, "/views/Dashboard.fxml");
    }
    @FXML
    public void initialize() {
        // Initialize the columns
        sourceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSource()));

        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());

        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDateTime();
            String formattedDateTime = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm:ss")) : "";
            return new SimpleStringProperty(formattedDateTime);
        });
        
        incomeList = incMan.getIncomeList(incomeList, null, null, "");
        double totalIncomes = incMan.getTotalIncome(incomeList);
        totalIncomeLabel.setText("Total Income: $ "+ totalIncomes);
        incomesTable.setItems(incomeList);

    }
    @FXML
    private void updateTable(ActionEvent e) {
    	updateTable();
    }
    private void updateTable() {
        LocalDate selectedDate = startDateField.getValue();
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime startDate = (selectedDate == null)? null: LocalDateTime.of(selectedDate, time);   
        
        selectedDate = endDateField.getValue();
        time = LocalTime.of(23, 59, 59);
        LocalDateTime endDate = (selectedDate == null)? null: LocalDateTime.of(selectedDate, time);
        
        String source = (sourceField.getText().isBlank())? "": sourceField.getText();
        incomeList = incMan.getIncomeList(incomeList, startDate, endDate, source);
        double totalIncome = incMan.getTotalIncome(incomeList);
        totalIncomeLabel.setText("Total Income: $ "+ totalIncome);
        incomesTable.setItems(incomeList);    	
    }
    
    @FXML 
    private void updateAccSource(KeyEvent event) {
    	KeyCode key = event.getCode();
    	if(key == KeyCode.ENTER) {
    		updateTable();
    	}
    }


}