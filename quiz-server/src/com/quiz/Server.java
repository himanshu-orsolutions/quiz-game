package com.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.quiz.models.Question;
import com.quiz.models.Quiz;
import com.quiz.utils.QuizScriptParser;

/**
 * The class Server. It holds implementation of the quiz server which can handle
 * multiple players simultaneously.
 */
public class Server {

	/**
	 * The server socket
	 */
	private ServerSocket serverSocket;

	/**
	 * The quiz content
	 */
	private String quizContent;

	/**
	 * The quiz metadata
	 */
	private HashMap<String, String> metadata = new HashMap<>();

	/**
	 * The total number of questions
	 */
	private Integer totalQuestions = 0;

	/**
	 * The task executor. It handle the exams.
	 */
	private ExecutorService taskExecutor = Executors.newCachedThreadPool();

	/**
	 * Prepares the quiz metadata
	 * 
	 * @param quiz The quiz object
	 */
	private void prepareQuizMetadata(Quiz quiz) {

		List<Question> questions = quiz.getQuestions();
		questions.forEach(question -> {
			if (!metadata.containsKey(question.getId())) {
				metadata.put(question.getId(), question.getAnswer());
			} else {
				// TODO: log it
			}
			question.setAnswer(null);
		});

		totalQuestions = metadata.size(); // Setting the total questions
	}

	/**
	 * Prepares the final result
	 * 
	 * @param correct The correct answers count
	 * @param wrong   The wrong answers count
	 * @return The final result
	 */
	private String prepareFinalResult(Integer correct, Integer wrong) {

		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append("Right answer(s): " + correct + ", ");
		resultBuilder.append("Wrong answer(s): " + wrong + ", ");
		resultBuilder.append("Total percentage: " + (correct / (double) totalQuestions) * 100.0);

		return resultBuilder.toString();
	}

	/**
	 * Calculates result from the result request
	 * 
	 * @param resultRequest The result request. Its in the form of question ID
	 *                      1:Answer, question ID 2:Answer, ..., question ID
	 *                      N:Answer,
	 * @return The calculated result
	 */
	private String calculateResult(String resultRequest) {

		String[] splitted = resultRequest.split(",");
		Integer correct = 0;
		Integer wrong = 0;

		if (splitted.length == totalQuestions) {
			for (String info : splitted) {
				String[] idAndAnswer = info.split(":");

				if (idAndAnswer.length == 2) {
					String id = idAndAnswer[0].trim();
					String answer = idAndAnswer[1].trim();
					if (metadata.containsKey(id)) {
						if (metadata.get(id).equals(answer)) {
							correct++;
						} else {
							wrong++;
						}
					} else {
						// TODO: log it
					}
				} else {
					// TODO: log it
				}
			}
		} else {
			// TODO: log it
		}

		// Preparing the final result
		return prepareFinalResult(correct, wrong);
	}

	/**
	 * Starts the exam
	 */
	private void startQuiz(BufferedReader inputReader, BufferedWriter outputWriter) {

		// Sending the question paper
		taskExecutor.execute(() -> {
			try {
				outputWriter.write(quizContent + "\n");
				outputWriter.flush();

				String resultRequest = inputReader.readLine();
				while (resultRequest != null) {
					String result = calculateResult(resultRequest);
					outputWriter.write(result + "\n");
					outputWriter.flush();
				}
			} catch (IOException ioException) {
				// TODO: log it
			} finally {
				try {
					inputReader.close();
					outputWriter.close();
				} catch (IOException ioException) {
					// TODO: log it
				}
			}
		});
	}

	/**
	 * Starts listening to requests from the quiz players
	 */
	private void acceptConnections() {

		while (true) {
			try {
				Socket quizSocket = this.serverSocket.accept();
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(quizSocket.getInputStream()));
				BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(quizSocket.getOutputStream()));

				System.out.println("Connected to " + quizSocket.getInetAddress().getHostAddress());
				startQuiz(inputReader, outputWriter);

			} catch (IOException ioException) {
				// TODO: log it
			}
		}
	}

	/**
	 * Instantiating the server
	 */
	public Server() {
		try {
			serverSocket = new ServerSocket(9090);
			Quiz quiz = QuizScriptParser.parse(Paths.get("quiz.xml"));

			if (quiz != null) { // Starts accepting the connections
				prepareQuizMetadata(quiz);
				quizContent = QuizScriptParser.toXMLString(quiz);
				acceptConnections();
			} else {
				// TODO: log it
			}
		} catch (IOException ioException) {
			// TODO: log it
		}
	}

	/**
	 * The execution starts from here
	 * 
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		new Server();
	}
}
