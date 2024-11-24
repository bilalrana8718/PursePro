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

public class BudgetManager {
	
    private static BudgetManager instance;

	
    // Private constructor to prevent instantiation
    private BudgetManager() {}

    // Static method to get the singleton instance
    public static BudgetManager getInstance() {
        if (instance == null) {
            instance = new BudgetManager();
        }
        return instance;
    }
    
    
    public ObservableList<Budget> getBudgetList(ObservableList<Budget> budgetList) {
    	budgetList.clear();
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select * from Budget where UserID = "+UserID + " ORDER BY DateModified DESC";
    		try (Connection connection = DatabaseConnection.getConnection()) {
    			Statement statement = connection.createStatement();
    			ResultSet resultSet = statement.executeQuery(query);
    			
            // Iterate through the ResultSet and add Income objects to the ObservableList
            	while (resultSet.next()) {
            		int userID = resultSet.getInt("UserID");
            		int id = resultSet.getInt("BudgetID");
            		double amount = resultSet.getDouble("Amount");
            		String source = resultSet.getString("Category");
            		LocalDateTime dateTime = resultSet.getTimestamp("DateModified").toLocalDateTime();

            		// Add the Income object to the ObservableList
            		budgetList.add(new Budget(id, amount, source, dateTime,userID));
            	}
    		}
    	catch (SQLException e) {
            e.printStackTrace();
        }
    	return budgetList;
    }
    public void updateBudget(double amount, String category) {
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	try (Connection connection = DatabaseConnection.getConnection()) {
    		PreparedStatement updateBud = connection.prepareStatement("UPDATE Budget SET Amount = ?, DateModified=? WHERE category = ? and UserID = ?");
    		
    		updateBud.setDouble(1, amount);
    		updateBud.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
    		updateBud.setString(3, category);
    		updateBud.setInt(4, UserID);
    		
			int rowsAffected = updateBud.executeUpdate();
			
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Budget Updated!");
			} else {
				showAlert(Alert.AlertType.ERROR, "Error", "Updating Budget Failed!");
			}    		
    	}
    	catch (SQLException e) {
            e.printStackTrace();
        }        
   	
    }
    public void budAfterExpense(double expAmount, String category) {
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select Amount from Budget where UserID= " + UserID + " and category = '"+category+"'";
		try (Connection connection = DatabaseConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
        	if (resultSet.next()) {
        		double budgetAmount = resultSet.getInt("Amount");
        		if(budgetAmount == 0) {
        			return;
        		}
        		else {
        			double remainingAmount = budgetAmount- expAmount>0? budgetAmount-expAmount: 0;
        			
        			if(remainingAmount == 0) {
        				showAlert(Alert.AlertType.INFORMATION, "Success", "You Have Used all Budget of Category "+category);
        			}
        			updateBudget(remainingAmount, category);
        		}
        	}
		}
	catch (SQLException e) {
        e.printStackTrace();
    }     	
    }
    public void addBudgets(String email) { 
    	int UserID = 1;
    	String query = "select UserID from User where Email = '"+email+"'";
		try (Connection connection = DatabaseConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
        	if (resultSet.next()) {
        		UserID = resultSet.getInt("UserID");
        		System.out.println(UserID);
        		new Budget(0.0, "Groceries", UserID).insertToDataBase();
        		new Budget(0.0, "Rent", UserID).insertToDataBase();
        		new Budget(0.0, "Utilities", UserID).insertToDataBase();
        		new Budget(0.0, "Travel", UserID).insertToDataBase();
        		new Budget(0.0, "Miscellaneous", UserID).insertToDataBase();
        	}
		}
	catch (SQLException e) {
        e.printStackTrace();
    }    	
    	
    }
    
	private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
