package model;

import java.time.LocalDateTime;

public class FinancialHealthScore {
	private double score;
	private double incomeToExpenseRatio;
	private double savingPotential;
	private IncomeManager incomeManager;
	private ExpenseManager expenseManager;
	
	FinancialHealthScore(IncomeManager i, ExpenseManager e){
		incomeManager = i;
		expenseManager = e;
	}
	
	public void calculateHealthScore() {
		
		LocalDateTime today = LocalDateTime.now();
		//get total income for last 30 days
		double totalIncome = incomeManager.getTotalExpenses(today.minusDays(30), today);
		double totalExpenses = expenseManager.getTotalExpenses(today.minusDays(30), today);
		
		calculateFinancialHealthScore(totalIncome, totalExpenses);
		
		
		
		generateSuggestions();
	}
	
	private void calculateFinancialHealthScore(double totalIncome, double totalExpense) {
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
		
		
		if(savingPotential >= 20) score += 50;
		else if(savingPotential >= 10 && savingPotential <20) {
			//score point in range 35-49
			
			score += (savingPotential - 10)/(20 - 10) * (50 - 35)   +  35;
		}
		else {
			score += (savingPotential - 0)/(10 - 0) * (35 - 0);		
		}
	}
	
	
	public void generateSuggestions() {
		
	}
}
