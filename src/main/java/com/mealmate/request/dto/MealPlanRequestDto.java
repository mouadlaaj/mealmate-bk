package com.mealmate.request.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MealPlanRequestDto {

	@NotBlank(message = "Name is required")
	@Size(max = 100)
	private String name;

	@NotNull(message = "Start date is required")
	private LocalDate startDate;

	@NotNull(message = "End date is required")
	private LocalDate endDate;

	@Valid
	private List<MealPlanEntryRequestDto> entries;

	public MealPlanRequestDto() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public List<MealPlanEntryRequestDto> getEntries() {
		return entries;
	}

	public void setEntries(List<MealPlanEntryRequestDto> entries) {
		this.entries = entries;
	}
}
