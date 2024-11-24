
package controllers;

import db.DatabaseConnection;
import models.BudgetManager;
import models.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> accountTypeField;

    @FXML
    void initialize() {
        accountTypeField.getItems().addAll("Induvidual", "Organization");
        accountTypeField.setValue("Induvidual"); // Default selection
    }

    @FXML
    void handleSignUp(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String accountType = accountTypeField.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
        } else {
            User user = new User(username, email, password, phone, accountType);
            saveUserToDatabase(user);
        }
    }

    private void saveUserToDatabase(User user) {
        String insertQuery = "INSERT INTO User (UserName, Email, Password, Phone, TypeOfAccount) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setString(5, user.getTypeOfAccount());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Sign Up Successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Sign Up Failed!");
            }
            
            BudgetManager.getInstance().addBudgets(user.getEmail());

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
    
 // This method will be called when the 'Go to Login' button is clicked
    public void goToLogin(ActionEvent event) throws IOException {
        // Load the Login.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        Parent loginPage = loader.load();

        // Create a new scene with the Login page
        Scene scene = new Scene(loginPage, 800, 500);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        
        stage.show();
    }
}
