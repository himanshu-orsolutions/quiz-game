package com.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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

import com.google.gson.Gson;
import com.quiz.models.Player;
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
		questions.forEach(question -> { // Creating map of question ID to its answer
			if (!metadata.containsKey(question.getId())) {
				metadata.put(question.getId(), question.getAnswer());
			} else {
				System.out.println("Error: Duplicate question ID: " + question.getId());
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
	 * @param resultRequest The result request. Its in the the following format:
	 *                      {question ID 1:Answer, question ID 2:Answer, ...,
	 *                      question ID N:Answer,}
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
						System.out.println("Error: Invalid question ID found in result request: " + id);
					}
				} else {
					System.out.println("Error: Invalid format of answer: " + info);
				}
			}
		} else {
			System.out.println("Error: Answers to all questions are not found in result request.");
		}

		// Preparing the final result
		return prepareFinalResult(correct, wrong);
	}

	/**
	 * Saves player information to file
	 * 
	 * @param player The player
	 */
	private synchronized void saveToFile(Player player) throws IOException {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt", true))) {
			writer.append("First name: " + player.getFirstName());
			writer.append("\n");
			writer.append("Last name: " + player.getLastName());
			writer.append("\n");
			writer.append("Age: " + player.getAge());
			writer.append("\n");
			writer.append("Result: " + player.getResult());
			writer.append("\n");
		}
	}

	/**
	 * Starts the exam
	 */
	private void startQuiz(BufferedReader inputReader, BufferedWriter outputWriter) {

		// Sending the question paper
		taskExecutor.execute(() -> {
			try {
				// Getting the player information
				String playerInfo = inputReader.readLine();
				Player player = new Gson().fromJson(playerInfo, Player.class);

				// Sending the quiz content to the player
				outputWriter.write(quizContent + "\n");
				outputWriter.flush();

				// Getting the result request
				String resultRequest = inputReader.readLine();

				// Calculating the result
				String result = calculateResult(resultRequest);
				player.setResult(result);

				// Sending the result to player
				outputWriter.write(result + "\n");
				outputWriter.flush();

				// Saving the player information
				saveToFile(player);
			} catch (IOException ioException) {
				System.out.println("Error: Quiz interrupted. Exception: " + ioException.getLocalizedMessage());
			} finally {
				try {
					inputReader.close();
					outputWriter.close();
				} catch (IOException ioException) {
					System.out
							.println("Error: Streams closing failed. Exception: " + ioException.getLocalizedMessage());
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

				System.out.println("Connected to player: " + quizSocket.getInetAddress().getHostAddress());
				System.out.println("Starting the quiz...");
				startQuiz(inputReader, outputWriter);
			} catch (IOException ioException) {
				System.out.println(
						"Error: Connection with player interrupted. Exception: " + ioException.getLocalizedMessage());
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
				System.out.println("Error: Quiz content parsing failed. Cannot proceed further!!");
			}
		} catch (IOException ioException) {
			System.out.println("Error: Server cannot be started. Exception: " + ioException.getLocalizedMessage());
		}
	}

	/**
	 * The execution starts from here
	 * 
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		new Server(); // Starting the quiz server
	}
}
