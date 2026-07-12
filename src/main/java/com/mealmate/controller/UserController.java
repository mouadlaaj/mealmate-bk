package com.mealmate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealmate.request.dto.UpdateUserRequestDto;
import com.mealmate.response.dto.GenericMessage;
import com.mealmate.response.dto.UserResponseDto;
import com.mealmate.security.service.UserDetailsImpl;
import com.mealmate.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/user")
@SecurityRequirement(name = "token")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl currentUser) {
		LOGGER.info("Fetching profile for user ID: {}", currentUser.getId());
		UserResponseDto user = userService.getUserById(currentUser.getId());
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<UserResponseDto> updateCurrentUser(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@Valid @RequestBody UpdateUserRequestDto updateDto) {

		LOGGER.info("Updating profile for user ID: {}", currentUser.getId());
		UserResponseDto updated = userService.updateUser(currentUser.getId(), updateDto);
		LOGGER.info("Profile updated successfully for user ID: {}", currentUser.getId());
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<GenericMessage> logout(HttpServletRequest request,
			@AuthenticationPrincipal UserDetailsImpl currentUser) {
		Long loggedUserId = currentUser.getId();
		LOGGER.info("Attempting to log out user ID: {}", loggedUserId);
		GenericMessage logoutResponse = userService.logout(request);
		LOGGER.info("User logged out successfully, user ID: {}", loggedUserId);
		return new ResponseEntity<>(logoutResponse, HttpStatus.OK);
	}
}