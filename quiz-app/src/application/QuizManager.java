package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class QuizManager {

	/**
	 * The message consumer
	 */
	private Consumer<String> messageConsumer;

	/**
	 * The connection
	 */
	private Connection connection;

	/**
	 * Instantiating the Quiz Manager
	 * 
	 * @param host            The host
	 * @param port            The port
	 * @param messageConsumer The message consumer
	 */
	public QuizManager(String host, int port, Consumer<String> messageConsumer) {

		this.messageConsumer = messageConsumer;
		this.connection = new Connection(host, port);
		this.connection.start();
	}

	/**
	 * Sends the message
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {

		try {
			connection.messageWriter.write(message + "\n");
			connection.messageWriter.flush();
		} catch (IOException ioException) {
			System.out.println("Error sending the message to server!!");
		}
	}

	/**
	 * Ends the connection
	 */
	public void endConnection() {

		try {
			connection.socket.close();
		} catch (IOException ioException) {
			System.out.println("Error closing the connections!!");
		}
	}

	/**
	 * The class Connection.
	 */
	private class Connection extends Thread {

		/**
		 * The host
		 */
		private String host;

		/**
		 * The port
		 */
		private int port;

		/**
		 * The connection socket
		 */
		private Socket socket;

		/**
		 * The buffered writer
		 */
		private BufferedWriter messageWriter;

		/**
		 * Instantiating the Connection
		 * 
		 * @param host the host
		 * @param port the port
		 */
		public Connection(String host, int port) {
			this.host = host;
			this.port = port;
		}

		/**
		 * Runs the thread
		 */
		@Override
		public void run() {

			try (Socket socket = new Socket(host, port);
					BufferedReader messageReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					BufferedWriter messageWriter = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()))) {

				this.socket = socket;
				this.messageWriter = messageWriter;

				// Sending the player information
				sendMessage(WelcomePageController.playerInfo);

				while (true) {
					messageConsumer.accept(messageReader.readLine());
				}
			} catch (IOException ioException) {
				System.out.println("Connection closed!!");
			}
		}
	}
}
