package com.quiz.models;

import java.util.List;

/**
 * The model Question. It holds information of a question.
 */
public class Question {

	private String question;
	private List<String> options;
	private Integer answer;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public Integer getAnswer() {
		return answer;
	}

	public void setAnswer(Integer answer) {
		this.answer = answer;
	}
}
