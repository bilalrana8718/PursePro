package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import db.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.SessionManager;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = emailField.getText();
        String password = passwordField.getText();


        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
        } else {
            boolean isAuthenticated = authenticateUser(username, password);
            if (isAuthenticated) {
            	
                loadDashboard(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid email or password.");
            }
        }
    }

    private boolean authenticateUser(String email, String password) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                String userName = resultSet.getString("UserName");
                double accountBalance = resultSet.getDouble("AccountBalance"); // Addition made 
                
                
                SessionManager.getInstance().setCurrentUserId(userId);
                SessionManager.getInstance().setCurrentUserName(userName);
                SessionManager.getInstance().setCurrentUserEmail(email);
                SessionManager.getInstance().setCurrentUserBalance(accountBalance); // Addition  Store balance

                return true;
            }
            return resultSet.next(); 

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database Connection Failed: " + e.getMessage());
        }

        return false;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public void createAnAccount(ActionEvent event) throws IOException {
        // Load the Login.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignUp.fxml"));
        Parent SignUpPage = loader.load();

        Scene scene = new Scene(SignUpPage, 800, 500);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        
        stage.show();
    }
    
    private void loadDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(dashboardRoot, 800, 500);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the Dashboard.");
        }
    }

}