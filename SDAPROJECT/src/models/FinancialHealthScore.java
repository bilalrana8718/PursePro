package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import db.DatabaseConnection;
import javafx.scene.control.Alert;

public class FinancialHealthScore {
	private static FinancialHealthScore instance;
	private double score;
	private double incomeToExpenseRatio;
	private double savingPotential;
	private IncomeManager incomeManager = IncomeManager.getInstance();
	private ExpenseManager expenseManager = ExpenseManager.getInstance();
	private String suggestion = "";
	
	
	private FinancialHealthScore() {}
	
	public static FinancialHealthScore getInstance() {
		if(instance == null) {
			instance = new FinancialHealthScore();
		}
		return instance;
	}
	
	public void calculateHealthScore() {
		
		LocalDateTime today = LocalDateTime.now();
		//get total income for last 30 days
		double totalIncome = incomeManager.getTotalIncome(today.minusDays(30), today, "");
		double totalExpenses = expenseManager.getTotalExpense(today.minusDays(30), today, "all");
		
		calculateFinancialHealthScore(totalIncome, totalExpenses);	
		
		generateSuggestions();
	}
	
	private void calculateFinancialHealthScore(double totalIncome, double totalExpense) {
		if(totalExpense == 0) totalExpense = 1;
		if(totalIncome == 0) 	totalIncome = 1;
		incomeToExpenseRatio = totalIncome/totalExpense;
		savingPotential = (totalIncome-totalExpense)*100/totalIncome;
		
		score= 0;
		if(incomeToExpenseRatio >= 1.2) score += 50;
		else if(incomeToExpenseRatio >= 1 && incomeToExpenseRatio < 1.2) {
			// score in range 35 to 49 
			
			score += (incomeToExpenseRatio - 1)/(1.2 - 1) * (50 - 35)   +  35;
		}
		else {
			//score in range 0 to 34
			score += (incomeToExpenseRatio - 0)/(1 - 0) * (35 - 0);
			
		}
		
		
		if(savingPotential < 0) score += 0;
		else if(savingPotential >= 20) score += 50;
		else if(savingPotential >= 10 && savingPotential <20) {
			//score point in range 35-49
			
			score += (savingPotential - 10)/(20 - 10) * (50 - 35)   +  35;
		}
		else {
			score += (savingPotential - 0)/(10 - 0) * (35 - 0);		
		}
	}
	
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getIncomeToExpenseRatio() {
		return incomeToExpenseRatio;
	}

	public void setIncomeToExpenseRatio(double incomeToExpenseRatio) {
		this.incomeToExpenseRatio = incomeToExpenseRatio;
	}

	public double getSavingPotential() {
		return savingPotential;
	}

	public void setSavingPotential(double savingPotential) {
		this.savingPotential = savingPotential;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void generateSuggestions() {

		suggestion = "";
	    // Suggestion based on income-to-expense ratio
	    if (incomeToExpenseRatio < 1) {
	        suggestion += "1. Try to reduce your expenses or increase your income to ensure your expenses do not exceed your income.";
	    } else if (incomeToExpenseRatio >= 1 && incomeToExpenseRatio < 1.2) {
	    	suggestion += "1. Your income-to-expense ratio is healthy but could improve. Consider reviewing your budget to optimize savings.";
	    } else {
	    	suggestion += "1. Great job maintaining a high income-to-expense ratio! Keep monitoring your expenses to maintain this balance.";
	    }

	    // Suggestion based on saving potential
	    if (savingPotential < 0) {
	    	suggestion += (" 2. You are spending more than you earn. Focus on eliminating unnecessary expenses and setting realistic saving goals.");
	    } else if (savingPotential >= 0 && savingPotential < 10) {
	    	suggestion += (" 2. Your saving potential is low. Try saving at least 10% of your income to build a safety net.");
	    } else if (savingPotential >= 10 && savingPotential < 20) {
	    	suggestion += (" 2. You are on the right track with saving. Aim to increase your savings to 20% for a more robust financial future.");
	    } else {
	    	suggestion += (" 2. Excellent saving potential! Consider investing part of your savings to grow your wealth.");
	    }

	    // Suggestion based on overall financial health score
	    if (score < 35) {
	    	suggestion += (" 3. Your financial health score is low. Focus on reducing debts, managing expenses, and improving savings.");
	    } else if (score >= 35 && score < 70) {
	    	suggestion += (" 3. Your financial health is average. Work on gradually increasing your savings and optimizing your expenses.");
	    } else {
	    	suggestion += (" 3. Your financial health is excellent! Continue to maintain good habits and explore opportunities to invest or grow your wealth.");
	    }
	}
	
	public void insertToDataBase() {
		String insertQuery = "INSERT INTO FinancialHealthScore (UserID, IncomeToExpenseRatio, SavingsPotentialPercentage, OverallScore,Suggestions, Date) VALUES (?, ?, ?, ?, ?, ?)";
		int userID = SessionManager.getInstance().getCurrentUserId();
		
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
			
			preparedStatement.setInt(1, userID);
			preparedStatement.setDouble(2, incomeToExpenseRatio);
			preparedStatement.setDouble(3, this.savingPotential);
			preparedStatement.setDouble(4, this.score);
			preparedStatement.setString(5, this.suggestion);
			preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
			
			int rowsAffected = preparedStatement.executeUpdate();
			
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "score added to records!");
			} else {
				showAlert(Alert.AlertType.ERROR, "Error", "adding score Failed!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Database Connection Failed: " + e.getMessage());
		}		
	}
	private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}