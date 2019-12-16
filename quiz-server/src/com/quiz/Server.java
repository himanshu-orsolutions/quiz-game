package com.quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	 * The question paper
	 */
	private String questionPaper = "";

	/**
	 * The task executor. It handle the exams.
	 */
	private ExecutorService taskExecutor = Executors.newCachedThreadPool();

	/**
	 * Starts the exam
	 */
	private void startExam(BufferedReader inputReader, BufferedWriter outputWriter) {

		// Sending the question paper
		taskExecutor.execute(() -> {
			try {
				outputWriter.write(questionPaper + "\n");
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

				this.startExam(inputReader, outputWriter);
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
			this.serverSocket = new ServerSocket(9090);
		} catch (IOException ioException) {
			// TODO: log it
		}
	}

}
