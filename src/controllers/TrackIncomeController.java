package controllers;

import javafx.fxml.FXML;

import models.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Income; // Model class for Income (you need to create this class)
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DatabaseConnection;

public class TrackIncomeController {

    @FXML
	private TableView<Income> incomesTable;
    @FXML private TableColumn<Income, String> sourceColumn;
    @FXML private TableColumn<Income, Double> amountColumn;
    @FXML private TableColumn<Income, String> dateTimeColumn;
    @FXML private Label totalIncomeLabel;
    @FXML private Button addIncomeButton;

    private ObservableList<Income> incomeList = FXCollections.observableArrayList();


    // Calculate total income for the last 30 days
    private void calculateTotalIncome() {
        LocalDateTime today = LocalDateTime.now();
        double totalIncome = 0.0;

        // Filter incomes from the last 30 days and sum up the amount
        for (Income income : incomeList) {
            if (income.getDateTime().isAfter(today.minusDays(30))) {
                totalIncome += income.getIncomeAmount();
            }
        }

        // Display total income in the label
        totalIncomeLabel.setText("Total Income in Last 30 Days: $" + totalIncome);
    }

    @FXML
    public void navigateToAddIncome(ActionEvent event) {
    	try {	
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Income.fxml"));
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
    private void backToDashBoard(ActionEvent event){
    	try {	
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
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
    public void initialize() {
        // Initialize the columns
        sourceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSource()));

        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());

//        dateTimeColumn.setCellValueFactory(cellData -> {
//            LocalDateTime dateTime = cellData.getValue().getDateTime();
//            String formattedDateTime = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
//            return new SimpleStringProperty(formattedDateTime);
//        });
//        
        populateTableWithData();

        calculateTotalIncome();
        incomesTable.setItems(incomeList);

    }


    private void populateTableWithData() {
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select * from Incomes where UserID = "+UserID;
    		try (Connection connection = DatabaseConnection.getConnection()) {
    			Statement statement = connection.createStatement();
    			ResultSet resultSet = statement.executeQuery(query);
    			
            // Iterate through the ResultSet and add Income objects to the ObservableList
            	while (resultSet.next()) {
            		int userID = resultSet.getInt("UserID");
            		int id = resultSet.getInt("IncomeID");
            		double amount = resultSet.getDouble("Amount");
            		String source = resultSet.getString("Source");
            		LocalDateTime dateTime = resultSet.getTimestamp("DateTime").toLocalDateTime();

            		// Add the Income object to the ObservableList
            		incomeList.add(new Income(id, amount, source, dateTime,userID));
            		
            	}
    		}
    	catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public double getTotalIncomeOfLast(int days) {
    	initialize();
        LocalDateTime today = LocalDateTime.now();
        double totalIncome = 0.0;

        // Filter incomes from the last 30 days and sum up the amount
        for (Income income : incomeList) {
            if (income.getDateTime().isAfter(today.minusDays(30))) {
                totalIncome += income.getIncomeAmount();
            }
        }
        return totalIncome;
    }



}

