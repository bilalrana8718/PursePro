package models;

import java.lang.ref.Reference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

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
            		double amount = resultSet.getDouble("RemainingAmount");
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
    public void updateBudget(double totalAmount, double amount, String category) {
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	try (Connection connection = DatabaseConnection.getConnection()) {
    		PreparedStatement updateBud = connection.prepareStatement("UPDATE Budget SET TotalAmount = ?, RemainingAmount = ?, DateModified=? WHERE category = ? and UserID = ?");
    		
    		updateBud.setDouble(1, totalAmount);
    		updateBud.setDouble(2, amount);
    		updateBud.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
    		updateBud.setString(4, category);
    		updateBud.setInt(5, UserID);
    		
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
    	String query = "select TotalAmount, RemainingAmount from Budget where UserID= " + UserID + " and category = '"+category+"'";
		try (Connection connection = DatabaseConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
        	if (resultSet.next()) {
        		double budgetAmount = resultSet.getDouble("TotalAmount");
        		double preRemainingAmount = resultSet.getDouble("RemainingAmount");
        		
        		if(preRemainingAmount == 0) {
        			return;
        		}
        		else {
        			double remainingAmount = preRemainingAmount- expAmount>0? preRemainingAmount-expAmount: 0;
        			
        			if(remainingAmount == 0) {
        				showAlert(Alert.AlertType.INFORMATION, "Success", "You Have Used all Budget of Category "+category);
        			}
        			else if(remainingAmount <= budgetAmount*.2) {
        				showAlert(Alert.AlertType.INFORMATION, "Success", "You Have Used more than 80% of Budget of Category "+category);        				
        			}
        			updateBudget(budgetAmount, remainingAmount, category);
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
    public void getAmount(AtomicReference<Double> totalAmount, AtomicReference<Double> remainingAmount, String category) {
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select TotalAmount, RemainingAmount from Budget where UserID= " + UserID + " and category = '"+category+"'";
		try (Connection connection = DatabaseConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
        	if (resultSet.next()) {
        		totalAmount.set(resultSet.getDouble("TotalAmount"));
        		remainingAmount.set(resultSet.getDouble("RemainingAmount"));
        		}
		}
	catch (SQLException e) {
        e.printStackTrace();
    }    	
    	
    }
    public boolean budgetExists(String category, int UserID) {
    	String query = "select * from Budget where UserID= " + UserID + " and category = '"+category+"'";
		try (Connection connection = DatabaseConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
        	if (resultSet.next()) {
        		return true;
        	}
        	return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}    	
		return false;
    }
    public void addBudget(double totalAmount, double remainingAmount, String category) {
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	if(this.budgetExists(category, UserID)) {
			showAlert(Alert.AlertType.ERROR, "Error", "Budget of the category already exists!");
    		return;
    	}
    	
    	new Budget(totalAmount, category, UserID).insertToDataBase();
    }
    public void removeBudget(String category) {
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	if(!this.budgetExists(category, UserID)) {
			showAlert(Alert.AlertType.ERROR, "Error", "Budget of the category doesn't exists!");
    		return;
    	}    
    	
    	try (Connection connection = DatabaseConnection.getConnection()) {
    		PreparedStatement removeBud = connection.prepareStatement("Delete from Budget WHERE category = ? and UserID = ?");
    		
    		removeBud.setString(1, category);
    		removeBud.setInt(2, UserID);
    		
			int rowsAffected = removeBud.executeUpdate();
			
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Budget Removed!");
			} else {
				showAlert(Alert.AlertType.ERROR, "Error", "Removing Budget Failed!");
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
