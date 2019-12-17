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
	private HashMap<String, Integer> metadata = new HashMap<>();

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
			}
			question.setAnswer(null);
		});
	}

	/**
	 * Starts the exam
	 */
	private void startExam(BufferedReader inputReader, BufferedWriter outputWriter) {

		// Sending the question paper
		taskExecutor.execute(() -> {
			try {
				outputWriter.write(quizContent + "\n");
				outputWriter.flush();

				String input = inputReader.readLine();
				while (input != null) {
					// Map it to the model and calculate result and send it again
				}
			} catch (IOException ioException) {
				// TODO: Log it
			}
		});
	}

	/**
	 * Starts listening to requests from the quiz players
	 */
	private void acceptConnections() {

		while (true) {
			try (Socket quizSocket = this.serverSocket.accept();
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(quizSocket.getInputStream()));
					BufferedWriter outputWriter = new BufferedWriter(
							new OutputStreamWriter(quizSocket.getOutputStream()))) {

				startExam(inputReader, outputWriter);
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

}
