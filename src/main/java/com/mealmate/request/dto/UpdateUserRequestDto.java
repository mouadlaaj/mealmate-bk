package com.mealmate.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestDto {

	@NotBlank(message = "First name is required")
	@Size(max = 50)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 50)
	private String lastName;


	public UpdateUserRequestDto() {
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

}
