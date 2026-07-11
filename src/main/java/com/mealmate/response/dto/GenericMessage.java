package com.mealmate.response.dto;

import java.time.LocalDateTime;

public class GenericMessage {

	private String message;
	private LocalDateTime time;

	public GenericMessage() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
}
