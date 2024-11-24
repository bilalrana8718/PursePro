package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import db.DatabaseConnection;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class ExpenseManager {
	
    private static ExpenseManager instance;

	
    // Private constructor to prevent instantiation
    private ExpenseManager() {}

    // Static method to get the singleton instance
    public static ExpenseManager getInstance() {
        if (instance == null) {
            instance = new ExpenseManager();
        }
        return instance;
    }
    
    
    public ObservableList<Expense> getExpenseList(ObservableList<Expense> expenseList, LocalDateTime startDate, LocalDateTime endDate, String category) {
    	expenseList.clear();
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select * from Expenses where UserID = "+UserID + " ORDER BY DateTime DESC";
    	if(startDate != null || endDate != null || category != "all") {
    		String conditions = "";
    		if(startDate != null) {
    			conditions += " and DateTime >= '"+Timestamp.valueOf(startDate)+"'";
    		}
    		if(endDate != null) 
    			conditions += " and DateTime <= '"+Timestamp.valueOf(endDate)+"'";
    		if(category != "all")
    			conditions += " and Category = '"+ category+ "'";
    		query = "select * from Expenses where UserID = "+UserID + conditions + " ORDER BY DateTime DESC";
    	}
    		try (Connection connection = DatabaseConnection.getConnection()) {
    			Statement statement = connection.createStatement();
    			ResultSet resultSet = statement.executeQuery(query);
    			
            // Iterate through the ResultSet and add Income objects to the ObservableList
            	while (resultSet.next()) {
            		int userID = resultSet.getInt("UserID");
            		int id = resultSet.getInt("ExpenseID");
            		double amount = resultSet.getDouble("Amount");
            		String source = resultSet.getString("Category");
            		LocalDateTime dateTime = resultSet.getTimestamp("DateTime").toLocalDateTime();

            		// Add the Income object to the ObservableList
            		expenseList.add(new Expense(id, amount, source, dateTime,userID));
            	}
    		}
    	catch (SQLException e) {
            e.printStackTrace();
        }
    	return expenseList;
    }
    
    public double getTotalExpense(ObservableList<Expense> expenseList) {
    	double totalExpenses = 0;
    	
    	for(Expense expense: expenseList) {
    		totalExpenses += expense.getAmount();
    	}
    	
    	return totalExpenses;
    }
    public double getTotalExpense(LocalDateTime startDate, LocalDateTime endDate, String category) {
    	double sum =0;
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select SUM(Amount) as total from Expenses where UserID = "+UserID;
    	if(startDate != null || endDate != null || category != "all") {
    		String conditions = "";
    		if(startDate != null) {
    			conditions += " and DateTime >= '"+Timestamp.valueOf(startDate)+"'";
    		}
    		if(endDate != null) 
    			conditions += " and DateTime <= '"+Timestamp.valueOf(endDate)+"'";
    		if(category != "all")
    			conditions += " and Category = '"+category+"'";
    		query = "select SUM(Amount) as total from Expenses where UserID = "+UserID + conditions;
    	}
    		try (Connection connection = DatabaseConnection.getConnection()) {
    			Statement statement = connection.createStatement();
    			ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                	sum = resultSet.getDouble("total"); // Use the alias name from the query
                    
                }
    			
    		}
    	catch (SQLException e) {
            e.printStackTrace();
        }   
    		
    	return sum;
    }
    
    public void removeExpense(String IDText) {
    	try {
    		int id = Integer.parseInt(IDText);
    		int UserID = SessionManager.getInstance().getCurrentUserId();
    		try (Connection connection = DatabaseConnection.getConnection();
    				PreparedStatement preparedStatement = connection.prepareStatement("Delete from Expenses where ExpenseId = ? and UserID = ?")) {

    			preparedStatement.setInt(1, id);
    			preparedStatement.setInt(2, UserID);
    			preparedStatement.executeUpdate();

    		}
    		catch (Exception e) {

    		}    		
    		
    		
    	}
    	catch(Exception e) {
    		
    	}
    }
}
