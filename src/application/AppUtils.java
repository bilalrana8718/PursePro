package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class AppUtils {
	 public static void changeScene(ActionEvent event, String fxmlPath) {
	        try {
	            FXMLLoader loader = new FXMLLoader(AppUtils.class.getResource(fxmlPath));
	            Parent root = loader.load();
	            // Get the current stage (window) from the event
	            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

	            // Set the new scene to the stage
	            Scene scene = new Scene(root, 800, 500);
	            stage.setScene(scene);
	            stage.setTitle("PursePro");
	            Image icon = new Image(AppUtils.class.getResourceAsStream("/views/PUR.png"));
                stage.getIcons().add(icon);
	            
	            stage.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    
	    }
	 
	 
}
