package com.mealmate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mealmate.entity.User;
import com.mealmate.exception.AlreadyExistsException;
import com.mealmate.exception.AppException;
import com.mealmate.repository.UserRepository;
import com.mealmate.request.dto.LoginRequestDto;
import com.mealmate.request.dto.RegisterRequestDto;
import com.mealmate.response.dto.LoginResponseDto;
import com.mealmate.response.dto.UserResponseDto;
import com.mealmate.security.JwtUtil;
import com.mealmate.security.service.UserDetailsImpl;

@Service
public class AuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	public UserResponseDto registerUser(RegisterRequestDto registerDto) {
		if (userRepo.existsByEmail(registerDto.getEmail())) {
			LOGGER.error("Registration failed, email already exists: {}", registerDto.getEmail());
			throw new AlreadyExistsException("Email already exists");
		}

		try {
			User user = new User();
			user.setFirstName(registerDto.getFirstName().trim());
			user.setLastName(registerDto.getLastName().trim());
			user.setEmail(registerDto.getEmail().trim().toLowerCase());
			user.setPassword(encoder.encode(registerDto.getPassword()));

			User saved = userRepo.save(user);
			return UserResponseDto.from(saved);

		} catch (Exception e) {
			LOGGER.error("Exception occurred during user registration. Error: {}", e.getMessage(), e);
			throw new AppException("Registration failed. Please try again.");
		}
	}

	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			String accessToken = jwtUtil.generateAccessToken(userDetails);

			LoginResponseDto responseDto = new LoginResponseDto();
			responseDto.setId(userDetails.getId());
			responseDto.setFirstName(userDetails.getFirstName());
			responseDto.setLastName(userDetails.getLastName());
			responseDto.setEmail(userDetails.getEmail());
			responseDto.setAccessToken(accessToken);

			return responseDto;

		} catch (Exception e) {
			LOGGER.error("Login failed for email: {}, Error: {}", loginRequestDto.getEmail(), e.getMessage());
			throw new AppException("Invalid email or password");
		}
	}


}