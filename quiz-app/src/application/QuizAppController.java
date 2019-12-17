package application;

import java.util.List;

import application.models.Question;
import application.models.Quiz;
import application.utils.QuizScriptParser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

public class QuizAppController {

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

	private void setQuestion(Integer currentIndex) {

		Question question = questions.get(currentIndex);

		questionArea.setText(question.getQuestion());
		List<String> options = question.getOptions();
		optionA.setText(options.get(0));
		optionB.setText(options.get(1));
		optionC.setText(options.get(2));
		optionD.setText(options.get(3));

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
	 * The quiz manager
	 */
	private QuizManager quizManager = new QuizManager("localhost", 9090, message -> {

		Platform.runLater(() -> {
			if (message.startsWith("<?xml")) {
				Quiz quiz = QuizScriptParser.parse(message);
				questions = quiz.getQuestions();
				totalQuestions = questions.size();
				setQuestion(currentIndex);
			} else {
				String[] splitted = message.split(",");
				StringBuilder resultBuilder = new StringBuilder();
				resultBuilder.append(splitted[0].trim());
				resultBuilder.append("\n");
				resultBuilder.append(splitted[1].trim());
				resultBuilder.append("\n");
				resultBuilder.append(splitted[2].trim());
				resultBuilder.append("\n");
				new Alert(AlertType.INFORMATION, resultBuilder.toString(), ButtonType.CLOSE).show();
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

	@FXML
	private void submit(ActionEvent event) {

		questions.get(currentIndex).setAnswer(((RadioButton) optionsGroup.getSelectedToggle()).getText());

		if (currentIndex == (totalQuestions - 1)) {
			String resultRequest = prepareResultRequest();
			quizManager.sendMessage(resultRequest);
		}
	}
}
