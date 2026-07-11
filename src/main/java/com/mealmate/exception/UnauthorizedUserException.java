package com.mealmate.exception;
public class UnauthorizedUserException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UnauthorizedUserException(String message) {
		super(message);
	}
}
