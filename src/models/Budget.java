package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import db.DatabaseConnection;
import javafx.scene.control.Alert;

public class Budget {
	private int userID;
	private int ID;
	private double amount;
	private String category;
	private LocalDateTime dateTime; //use localDateTime.of(YY, MM, DD, hour, min, sec) function to set time of your choice
	//use localDateTime.now() to set date time of your local system
	
	public Budget(double a, String c, LocalDateTime d){
		amount = a;
		category = c;
		dateTime = d;
	}
	public Budget(double a, String c, int UserID){
		amount = a;
		category = c;
		userID = UserID;
		dateTime = LocalDateTime.now();		
	}
	public Budget(int id, double a, String c, LocalDateTime d, int UserID){
		ID = id;
		amount = a;
		category = c;
		dateTime = d;
		userID = UserID;
	}	
	public void insertToDataBase() {
		String insertQuery = "INSERT INTO Budget (Category, RemainingAmount, dateModified, UserID, TotalAmount) VALUES (?, ?, ?, ?,0)";
		
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
			
			preparedStatement.setString(1, category);
			preparedStatement.setDouble(2, amount);
			preparedStatement.setTimestamp(3, Timestamp.valueOf(dateTime));
			preparedStatement.setInt(4, userID);
			
			preparedStatement.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Database Connection Failed: " + e.getMessage());
		}
	}
    public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
