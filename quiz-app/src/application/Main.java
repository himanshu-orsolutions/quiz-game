package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	/**
	 * Starts the application
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("WelcomePage.fxml"));
			BorderPane root = (BorderPane) loader.load();
			Scene scene = new Scene(root, 450, 230);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Quiz App");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execution starts from here
	 * 
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
