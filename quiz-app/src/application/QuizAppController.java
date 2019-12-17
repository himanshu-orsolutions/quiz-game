package application;

import java.util.List;

import application.models.Question;
import application.models.Quiz;
import application.utils.QuizScriptParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;

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

	/**
	 * The questions
	 */
	private List<Question> questions;

	/**
	 * The current question index
	 */
	private Integer currentIndex = 0;

	/**
	 * The quiz manager
	 */
	private QuizManager quizManager = new QuizManager("localhost", 9090, message -> {

		Platform.runLater(() -> {
			if (message.startsWith("<?xml")) {
				Quiz quiz = QuizScriptParser.parse(message);
				questions = quiz.getQuestions();

				questionArea.setText(questions.get(0).getQuestion());
				List<String> options = questions.get(0).getOptions();
				optionA.setText(options.get(0));
				optionB.setText(options.get(1));
				optionC.setText(options.get(2));
				optionD.setText(options.get(3));

				previousButton.setDisable(true);
				if (questions.size() == 1) {
					nextButton.setDisable(true);
				}
			} else {
				new Alert(AlertType.INFORMATION, message, ButtonType.CLOSE).show();
			}
		});
	});

}
