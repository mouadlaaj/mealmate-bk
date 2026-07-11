package com.mealmate.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

	private String message;

	private int code;

	private LocalDateTime timeStamp;



	public ErrorResponse(String message, int code, LocalDateTime timeStamp) {
		super();
		this.message = message;
		this.code = code;
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

}
