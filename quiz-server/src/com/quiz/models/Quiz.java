package com.quiz.models;

import java.util.List;

/**
 * The model Quiz. It holds information of all questions of a quiz.
 */
public class Quiz {

	private List<Question> questions;

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
