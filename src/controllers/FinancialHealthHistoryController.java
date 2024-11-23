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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.FinancialHealthScore;

public class FinancialHealthHistoryController {
	@FXML TableView<FinancialHealthScore> financialHealthTable;
	@FXML TableColumn<FinancialHealthScore, Double> incomeToExpenseColumn;
	@FXML TableColumn<FinancialHealthScore, Double> savingPotentialColumn;
	@FXML TableColumn<FinancialHealthScore, Double> scoreColumn;
	@FXML TableColumn<FinancialHealthScore, String> dateColumn;
	
    private ObservableList<FinancialHealthScore> scoreList = FXCollections.observableArrayList();

	private FinancialHealthScore calculator = FinancialHealthScore.getInstance();

	
    @FXML
    private void backToFinancialCalculator(ActionEvent event) {
    	AppUtils.changeScene(event, "/views/FinancialHealth.fxml");
    }
    @FXML
    public void initialize() {
    	
    	incomeToExpenseColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getIncomeToExpenseRatio()).asObject());
    	savingPotentialColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSavingPotential()).asObject());
    	scoreColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getScore()).asObject());
    	dateColumn.setCellValueFactory(cellData -> {
    		LocalDateTime dateTime = cellData.getValue().getDate();
    		String formattedDateTime = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm:ss")) : "";
    		return new SimpleStringProperty(formattedDateTime);
    	});    	
    	
    	scoreList = calculator.getScoreList(scoreList);
    	financialHealthTable.setItems(scoreList);
    }

}
