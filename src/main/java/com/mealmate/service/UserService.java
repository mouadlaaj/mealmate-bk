package com.mealmate.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mealmate.entity.User;
import com.mealmate.exception.AppException;
import com.mealmate.exception.NotFoundException;
import com.mealmate.repository.UserRepository;
import com.mealmate.request.dto.UpdateUserRequestDto;
import com.mealmate.response.dto.GenericMessage;
import com.mealmate.response.dto.UserResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	public UserResponseDto getUserById(Long userId) {
		User user = findUserOrThrow(userId);
		return UserResponseDto.from(user);
	}

	public UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateDto) {
		User user = findUserOrThrow(userId);

		try {
			user.setFirstName(updateDto.getFirstName().trim());
			user.setLastName(updateDto.getLastName().trim());

			User saved = userRepo.save(user);
			LOGGER.info("User updated successfully, ID: {}", saved.getId());
			return UserResponseDto.from(saved);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while updating user ID: {}, Error: {}", userId, e.getMessage(), e);
			throw new AppException("Update failed. Please try again.");
		}
	}

	private User findUserOrThrow(Long userId) {
		return userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
	}
	
	public GenericMessage logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();

		GenericMessage message = new GenericMessage();
		message.setMessage("Logout successful");
		message.setTime(LocalDateTime.now());
		return message;
	}
}