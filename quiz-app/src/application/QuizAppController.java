package application;

import java.io.IOException;
import java.util.List;

import application.models.Question;
import application.models.Quiz;
import application.utils.QuizScriptParser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class QuizAppController {

	// Welcome page controllers
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

	// Quiz page controllers
	@FXML
	TextArea questionArea;

	@FXML
	RadioButton optionA;

	@FXML
	RadioButton optionB;

	@FXML
	RadioButton optionC;

	@FXML
	RadioButton optionD;

	@FXML
	Button submitButton;

	@FXML
	Button nextButton;

	@FXML
	Button previousButton;

	@FXML
	ToggleGroup optionsGroup;

	/**
	 * The questions
	 */
	private List<Question> questions;

	/**
	 * The current question index
	 */
	private Integer currentIndex = 0;

	/**
	 * The total number of questions
	 */
	private Integer totalQuestions = 0;

	/**
	 * The quiz scene state
	 */
	private Boolean quizSceneLoaded = false;

	/**
	 * Prepares the result request to be sent to quiz server
	 * 
	 * @return The result request
	 */
	private String prepareResultRequest() {

		StringBuilder resultRequestBuilder = new StringBuilder();
		questions.forEach(question -> {
			resultRequestBuilder.append(question.getId());
			resultRequestBuilder.append(":");
			resultRequestBuilder.append(question.getAnswer());
			resultRequestBuilder.append(",");
		});

		return resultRequestBuilder.toString();
	}

	/**
	 * Shows the error message on pop-up
	 * 
	 * @param errorMessage The error message
	 */
	private void showErrorMessage(String errorMessage) {

		new Alert(AlertType.ERROR, errorMessage, ButtonType.CLOSE).show();
	}

	/**
	 * Sets the question on UI
	 * 
	 * @param currentIndex The index of current question
	 */
	private void setQuestion(Integer currentIndex) {

		Question question = questions.get(currentIndex);

		// Setting the question
		questionArea.setText("#" + (currentIndex + 1) + " " + question.getQuestion());
		List<String> options = question.getOptions();
		optionA.setText(options.get(0));
		optionB.setText(options.get(1));
		optionC.setText(options.get(2));
		optionD.setText(options.get(3));

		// Checking if an answer is already submitted, if yes, the previously submitted
		// answer will be shown
		if (question.getAnswer() != null) {
			if (options.get(0).equals(question.getAnswer())) {
				optionA.setSelected(true);
			} else if (options.get(1).equals(question.getAnswer())) {
				optionB.setSelected(true);
			} else if (options.get(2).equals(question.getAnswer())) {
				optionC.setSelected(true);
			} else {
				optionD.setSelected(true);
			}
		} else {
			optionA.setSelected(true);
		}

		// Button toggle operations
		if (currentIndex == 0 || currentIndex == (totalQuestions - 1)) {
			if (currentIndex == 0) {
				previousButton.setDisable(true);
				submitButton.setText("Submit");
			}
			if (currentIndex == (totalQuestions - 1)) {
				nextButton.setDisable(true);
				submitButton.setText("End Quiz");
			}
		} else {
			previousButton.setDisable(false);
			nextButton.setDisable(false);
			submitButton.setText("Submit");
		}
	}

	/**
	 * Ends the connection
	 */
	private void endConnection() {
		quizManager.endConnection();
	}

	/**
	 * The quiz manager
	 */
	private QuizManager quizManager = new QuizManager("localhost", 9090, message -> {

		Platform.runLater(() -> {
			if (message == null) {
				endConnection();
			} else {
				if (message.startsWith("<?xml")) { // The first input from server i.e. the quiz questions
					Platform.runLater(() -> {
						while (!quizSceneLoaded) {
							try {
								Thread.currentThread().wait(1000l);
							} catch (InterruptedException exception) {
								System.out.println("Error occurred while waiting for the quiz scene to load!");
							}
						}
						Quiz quiz = QuizScriptParser.parse(message);
						questions = quiz.getQuestions();
						totalQuestions = questions.size();
						setQuestion(currentIndex);
					});
				} else if (message.startsWith("Greetings")) {
					serverGreet.setText(message.substring(11));
				} else { // The result from server
					String[] splitted = message.split(",");
					StringBuilder resultBuilder = new StringBuilder();
					resultBuilder.append(splitted[0].trim());
					resultBuilder.append("\n");
					resultBuilder.append(splitted[1].trim());
					resultBuilder.append("\n");
					resultBuilder.append(splitted[2].trim());
					resultBuilder.append("\n");
					new Alert(AlertType.INFORMATION, resultBuilder.toString(), ButtonType.CLOSE).show(); // Showing
																											// result //
																											// on UI
				}
			}
		});
	});

	/**
	 * Goes to next question
	 * 
	 * @param event The event
	 */
	@FXML
	private void nextQuestion(ActionEvent event) {

		if (currentIndex < (totalQuestions - 1)) {
			currentIndex++;
			setQuestion(currentIndex);
		}
	}

	/**
	 * Goes to previous question
	 * 
	 * @param event The event
	 */
	@FXML
	private void prevQuestion(ActionEvent event) {

		if (currentIndex > 0) {
			currentIndex--;
			setQuestion(currentIndex);
		}
	}

	/**
	 * Submits the answer
	 * 
	 * @param event The event
	 */
	@FXML
	private void submit(ActionEvent event) {

		questions.get(currentIndex).setAnswer(((RadioButton) optionsGroup.getSelectedToggle()).getText());

		if (currentIndex == (totalQuestions - 1)) {
			String resultRequest = prepareResultRequest();
			quizManager.sendMessage(resultRequest);
		}
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
			quizManager.sendMessage( // Sending the player information to the server
					"{\"firstName\":\"" + firstName.getText() + "\",\"lastName\":\"" + lastName.getText()
							+ "\",\"age\":\"" + age.getText() + "\"}");
		}

		try {
			// Loading the quiz page
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("QuizApp.fxml"));
			BorderPane root = (BorderPane) loader.load();
			Scene scene = new Scene(root, 450, 230);
			Stage stage = new Stage();
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Quiz App");
			stage.show();
			quizSceneLoaded = true;
		} catch (IOException ioException) {
			showErrorMessage("Error loading the quiz");
		}
	}
}
