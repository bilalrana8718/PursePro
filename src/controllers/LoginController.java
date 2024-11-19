package controllers;

import db.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
                showAlert(Alert.AlertType.INFORMATION, "Success", "Login Successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid email or password.");
            }
        }
    }

    private boolean authenticateUser(String email, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
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
}
