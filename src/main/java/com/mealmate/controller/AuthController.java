package com.mealmate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealmate.request.dto.LoginRequestDto;
import com.mealmate.request.dto.RegisterRequestDto;
import com.mealmate.response.dto.LoginResponseDto;
import com.mealmate.response.dto.UserResponseDto;
import com.mealmate.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto registerDto) {
		 LOGGER.info("Registering new user with email: {}", registerDto.getEmail());
		UserResponseDto user = authService.registerUser(registerDto);
		LOGGER.info("User registered successfully with ID: {}", user.getId());
		 return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
		  LOGGER.info("Attempting to log in user with email: {}", loginDto.getEmail());
		LoginResponseDto loginResponse = authService.login(loginDto);
		LOGGER.info("User logged in successfully with email: {}", loginDto.getEmail());
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
	}

}
