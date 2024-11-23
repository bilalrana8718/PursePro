	package models;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import db.DatabaseConnection;
import javafx.beans.binding.NumberExpressionBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;

public class Income {
		private final ObjectProperty<LocalDateTime> date;
		private int UserID;
		private int ID;
		private double amount;
		private String source;
		private LocalDateTime dateTime; //use localDateTime.of(YY, MM, DD, hour, min, sec) function to set time of your choice
		//use localDateTime.now() to set date time of your local system
		
		public Income(double a, String s, LocalDateTime d, int uID){
			amount = a;
			source = s;
			dateTime = d;
			UserID = uID;
			date = new SimpleObjectProperty<>(dateTime);
			
		}
		public Income(double a, String s, int uID){
			amount = a;
			source = s;
			dateTime = LocalDateTime.now();
			UserID = uID;		
			date = new SimpleObjectProperty<>(dateTime);			
		}
		public Income(int id, double a, String s,LocalDateTime d, int uID){
			ID = id;
			amount = a;
			source = s;
			dateTime = LocalDateTime.now();
			UserID = uID;		
			date = new SimpleObjectProperty<>(dateTime);			
		}
		public void insertToDataBase() {
			String insertQuery = "INSERT INTO Incomes (Source, Amount, dateTime, UserID) VALUES (?, ?, ?, ?)";
			
			try (Connection connection = DatabaseConnection.getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
				
				preparedStatement.setString(1, source);
				preparedStatement.setDouble(2, amount);
				preparedStatement.setTimestamp(3, Timestamp.valueOf(dateTime));
				preparedStatement.setInt(4, UserID);
				
				int rowsAffected = preparedStatement.executeUpdate();
				
				if (rowsAffected > 0) {
					showAlert(Alert.AlertType.INFORMATION, "Success", "Income Added!");
				} else {
					showAlert(Alert.AlertType.ERROR, "Error", "adding Income Failed!");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Database Connection Failed: " + e.getMessage());
			}
		}
		public double getIncomeAmount() {
			return amount;
		}
		public int getID() {
			return ID;
		}
		public LocalDateTime getDateTime() {
			return dateTime;
		}
		
	    private void showAlert(Alert.AlertType alertType, String title, String content) {
	        Alert alert = new Alert(alertType);
	        alert.setTitle(title);
	        alert.setContentText(content);
	        alert.showAndWait();
	    }
		public int getUserID() {
			return UserID;
		}
		public void setUserID(int userID) {
			UserID = userID;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public void setID(int iD) {
			ID = iD;
		}
		public void setDateTime(LocalDateTime dateTime) {
			this.dateTime = dateTime;
		}
		public ObjectProperty<LocalDateTime> dateProperty() { return date;
 }

	}
