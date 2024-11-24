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

public class IncomeManager {
	
    private static IncomeManager instance;

	
    // Private constructor to prevent instantiation
    private IncomeManager() {}

    // Static method to get the singleton instance
    public static IncomeManager getInstance() {
        if (instance == null) {
            instance = new IncomeManager();
        }
        return instance;
    }
    
    
    public ObservableList<Income> getIncomeList(ObservableList<Income> incomeList, LocalDateTime startDate, LocalDateTime endDate, String source) {
    	incomeList.clear();

    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select * from Incomes where UserID = "+UserID + " ORDER BY DateTime DESC";
    	if(startDate != null || endDate != null || source != "") {
    		String conditions = "";
    		if(startDate != null) {
    			conditions += " and DateTime >= '"+Timestamp.valueOf(startDate)+"'";
    		}
    		if(endDate != null) 
    			conditions += " and DateTime <= '"+Timestamp.valueOf(endDate)+"'";
    		if(source != "")
    			conditions += " and Source = '"+ source+ "'";
    		query = "select * from Incomes where UserID = "+UserID + conditions + " ORDER BY DateTime DESC";
    	}
    		try (Connection connection = DatabaseConnection.getConnection()) {
    			Statement statement = connection.createStatement();
    			ResultSet resultSet = statement.executeQuery(query);
    			
            // Iterate through the ResultSet and add Income objects to the ObservableList
            	while (resultSet.next()) {
            		int userID = resultSet.getInt("UserID");
            		int id = resultSet.getInt("IncomeID");
            		double amount = resultSet.getDouble("Amount");
            		String source1 = resultSet.getString("Source");
            		LocalDateTime dateTime = resultSet.getTimestamp("DateTime").toLocalDateTime();

            		// Add the Income object to the ObservableList
            		incomeList.add(new Income(id, amount, source1, dateTime,userID));
            	}
    		}
    	catch (SQLException e) {
            e.printStackTrace();
        }
    	return incomeList;
    }
    
    public double getTotalIncome(ObservableList<Income> incomeList) {
    	double totalIncomes = 0;
    	
    	for(Income income: incomeList) {
    		totalIncomes += income.getAmount();
    	}
    	
    	return totalIncomes;
    }
    public double getTotalIncome(LocalDateTime startDate, LocalDateTime endDate, String source) {
    	double sum =0;
    	int UserID = SessionManager.getInstance().getCurrentUserId();
    	String query = "select SUM(Amount) as total from Incomes where UserID = "+UserID;
    	if(startDate != null || endDate != null || source != "") {
    		String conditions = "";
    		if(startDate != null) {
    			conditions += " and DateTime >= '"+Timestamp.valueOf(startDate)+"'";
    		}
    		if(endDate != null) 
    			conditions += " and DateTime <= '"+Timestamp.valueOf(endDate)+"'";
    		if(source != "")
    			conditions += " and Source = '"+ source +"'";
    		query = "select SUM(Amount) as total from Incomes where UserID = "+UserID + conditions;
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
    
    
    public void removeIncome(String IDText) {
    	try {
    		int id = Integer.parseInt(IDText);
    		int UserID = SessionManager.getInstance().getCurrentUserId();
    		try (Connection connection = DatabaseConnection.getConnection();
    				PreparedStatement preparedStatement = connection.prepareStatement("Delete from Incomes where IncomeId = ? and UserID = ?")) {

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
