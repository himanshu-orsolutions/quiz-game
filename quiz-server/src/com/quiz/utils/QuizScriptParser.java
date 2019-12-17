package com.quiz.utils;

import java.io.File;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.quiz.models.Quiz;

/**
 * The utility class QuizScriptParser. It holds implementation to parse the XML
 * quiz script and map it to associated POJOs.
 */
public class QuizScriptParser {

	/**
	 * The JAX context
	 */
	private static JAXBContext jaxContext;

	/**
	 * The XML unmarshaller
	 */
	private static Unmarshaller unmarshaller;

	static {
		try {
			jaxContext = JAXBContext.newInstance(Quiz.class);
			unmarshaller = jaxContext.createUnmarshaller();
		} catch (JAXBException jaxbException) {
			// TODO: log it
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
			// TODO: log it
		}
		return null;
	}
}
