package com.quiz.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.quiz.models.Quiz;

/**
 * The utility class QuizScriptParser. It holds implementation to process quiz
 * scripts.
 */
public class QuizScriptParser {

	/**
	 * The XML unmarshaller
	 */
	private static Unmarshaller unmarshaller;

	/**
	 * The XML marshaller
	 */
	private static Marshaller marshaller;

	static {
		try {
			unmarshaller = JAXBContext.newInstance(Quiz.class).createUnmarshaller();
			marshaller = JAXBContext.newInstance(Quiz.class).createMarshaller();
		} catch (JAXBException jaxbException) {
			System.out.println("Error: The marsheller and/or unmarsheller creation failed. Exception: "
					+ jaxbException.getLocalizedMessage());
		}
	}

	private QuizScriptParser() {
		// Its a utility class. Instantiation is not allowed.
	}

	/**
	 * Parses the XML quiz script from specified path.
	 * 
	 * @param path The script path
	 * @return The Quiz object
	 */
	public static Quiz parse(Path path) {

		try {
			File xml = path.toFile();
			return (Quiz) unmarshaller.unmarshal(xml);
		} catch (JAXBException jaxbException) {
			System.out.println(
					"Error: Deserialization of XML content failed. Exception: " + jaxbException.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * Serializes the quiz object
	 * 
	 * @param quiz The quiz object
	 * @return The XML content
	 */
	public static String toXMLString(Quiz quiz) {

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			marshaller.marshal(quiz, outputStream);
			return outputStream.toString();
		} catch (JAXBException | IOException exception) {
			System.out.println("Error: Serialization of POJO failed. Exception: " + exception.getLocalizedMessage());
		}
		return "";
	}
}
