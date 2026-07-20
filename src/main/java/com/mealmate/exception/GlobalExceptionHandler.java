package com.mealmate.exception;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AppException.class)
	public ResponseEntity<ErrorResponse> handleAppException(AppException e) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyExists(AlreadyExistsException e) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now());
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedUserException ex) {
		ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());
		return new ResponseEntity<>(getErrorsMap(errors), HttpStatus.BAD_REQUEST);
	}

	private Map<String, List<String>> getErrorsMap(List<String> errors) {
		Map<String, List<String>> errorResponse = new HashMap<>();
		errorResponse.put("errors", errors);
		return errorResponse;
	}

}
