package com.mealmate.response.dto;

import java.time.LocalDateTime;

import com.mealmate.entity.User;

/**
 * Returned instead of the raw User entity so the password hash is never
 * exposed in an API response.
 */
public class UserResponseDto {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private LocalDateTime createdAt;

	public UserResponseDto() {
	}

	public static UserResponseDto from(User user) {
		UserResponseDto dto = new UserResponseDto();
		dto.setId(user.getId());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setEmail(user.getEmail());
		dto.setCreatedAt(user.getCreatedAt());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
