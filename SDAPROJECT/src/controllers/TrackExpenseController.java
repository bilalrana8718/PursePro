package controllers;

import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import models.Expense;
import models.ExpenseManager;
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


public class TrackExpenseController {
	ExpenseManager expMan = ExpenseManager.getInstance();
	@FXML
	private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private Label totalExpenseLabel;
    @FXML private Button addIncomeButton;
    @FXML private ComboBox<String> categoryField;
    @FXML private DatePicker startDateField;
    @FXML private DatePicker endDateField;
    

    private ObservableList<Expense> expenseList = FXCollections.observableArrayList();

    @FXML
    public void navigateToAddExpense(ActionEvent event) {
        AppUtils.changeScene(event, "/views/addExpense.fxml");
    }
    @FXML
    private void backToDashBoard(ActionEvent event){
        AppUtils.changeScene(event, "/views/Dashboard.fxml");
    }
    @FXML
    public void initialize() {
        // Initialize the columns
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
//        dateColumn.setCellValueFactory(cellData -> (cellData.getValue().dateProperty()).asString());

        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDateTime();
            String formattedDateTime = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm:ss")) : "";
            return new SimpleStringProperty(formattedDateTime);
        });
        
        
        categoryField.setItems(FXCollections.observableArrayList("Groceries", "Rent", "Utilities", "Travel", "Miscellaneous", "all"));
        
   
        expenseList = expMan.getExpenseList(expenseList, null, null, "all");
        double totalExpenses = expMan.getTotalExpense(expenseList);
        totalExpenseLabel.setText("Total Expenses: $ "+ totalExpenses);
        expenseTable.setItems(expenseList);

    }
    @FXML
    private void updateTable(ActionEvent e) {
        LocalDate selectedDate = startDateField.getValue();
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime startDate = (selectedDate == null)? null: LocalDateTime.of(selectedDate, time);   
        
        selectedDate = endDateField.getValue();
        time = LocalTime.of(23, 59, 59);
        LocalDateTime endDate = (selectedDate == null)? null: LocalDateTime.of(selectedDate, time);
        
        String category = (categoryField.getValue()== null)? "all": categoryField.getValue();
        
        expenseList = expMan.getExpenseList(expenseList, startDate, endDate, category);
        double totalExpenses = expMan.getTotalExpense(expenseList);
        totalExpenseLabel.setText("Total Expenses: $ "+ totalExpenses);
        expenseTable.setItems(expenseList);       
        
   }

}