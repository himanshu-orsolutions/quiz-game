package application.utils;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import application.models.Quiz;

/**
 * The utility class QuizScriptParser. It holds implementation to process quiz
 * scripts.
 */
public class QuizScriptParser {

	/**
	 * The XML unmarshaller
	 */
	private static Unmarshaller unmarshaller;

	static {
		try {
			unmarshaller = JAXBContext.newInstance(Quiz.class).createUnmarshaller();
		} catch (JAXBException jaxbException) {
			System.out.println(
					"Error: The unmarsheller creation failed. Exception: " + jaxbException.getLocalizedMessage());
		}
	}

	private QuizScriptParser() {
		// Its a utility class. Instantiation is not allowed.
	}

	/**
	 * Parses the XML quiz script from specified path.
	 * 
	 * @param content The quiz content
	 * @return The Quiz object
	 */
	public static Quiz parse(String content) {

		try {
			return (Quiz) unmarshaller.unmarshal(new ByteArrayInputStream(content.getBytes()));
		} catch (JAXBException jaxbException) {
			System.out.println(
					"Error: Deserialization of XML content failed. Exception: " + jaxbException.getLocalizedMessage());
		}
		return null;
	}
}
