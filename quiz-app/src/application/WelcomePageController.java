package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WelcomePageController {

	@FXML
	Label serverGreet;

	@FXML
	TextField firstName;

	@FXML
	TextField lastName;

	@FXML
	TextField age;

	@FXML
	Button proceed;

	/**
	 * The player info
	 */
	static String playerInfo = "{}";

	/**
	 * Shows the error message on pop-up
	 * 
	 * @param errorMessage The error message
	 */
	private void showErrorMessage(String errorMessage) {

		new Alert(AlertType.ERROR, errorMessage, ButtonType.CLOSE).show();
	}

	/**
	 * Proceeds to the quiz
	 * 
	 * @param event The event
	 */
	@FXML
	void proceed(ActionEvent event) {

		String playerFirstName = firstName.getText();
		String playerLastName = lastName.getText();
		Integer playerAge = 0;
		boolean goodToGo = true;

		try {
			playerAge = Integer.parseInt(age.getText());
			if (playerAge < 2 || playerAge > 120) {
				showErrorMessage("The age should be in range 2-120");
				goodToGo = false;
			}
		} catch (NumberFormatException numberFormatException) {
			showErrorMessage("Invalid age");
			goodToGo = false;
		}
		// Validating the input
		if (playerFirstName.equals("") || playerLastName.equals("") || playerAge.equals("")) {
			showErrorMessage("Fields cannot be empty");
			goodToGo = false;
		}

		if (goodToGo) {
			playerInfo = "{\"firstName\":\"" + firstName.getText() + "\",\"lastName\":\"" + lastName.getText()
					+ "\",\"age\": \"" + age.getText() + "\"}";

			try {
				// Closing the current page
				((Stage) proceed.getScene().getWindow()).close();

				// Loading the quiz page
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("QuizApp.fxml"));
				Scene scene = new Scene(loader.load(), 650, 442);
				Stage stage = new Stage();
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.setTitle("Quiz App");
				stage.show();
			} catch (IOException ioException) {
				showErrorMessage("Error loading the quiz");
			}
		}
	}
}
