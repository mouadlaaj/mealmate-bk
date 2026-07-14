package com.mealmate.request.dto;

import java.time.LocalDate;

import com.mealmate.constants.MealType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MealPlanEntryRequestDto {

	@NotNull(message = "Recipe is required")
	private Long recipeId;

	@NotNull(message = "Planned date is required")
	private LocalDate plannedDate;

	@NotNull(message = "Meal type is required")
	private MealType mealType;

	@Positive(message = "Servings must be greater than 0")
	private Integer servings;

	public MealPlanEntryRequestDto() {
	}

	public Long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	public LocalDate getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDate plannedDate) {
		this.plannedDate = plannedDate;
	}

	public MealType getMealType() {
		return mealType;
	}

	public void setMealType(MealType mealType) {
		this.mealType = mealType;
	}

	public Integer getServings() {
		return servings;
	}

	public void setServings(Integer servings) {
		this.servings = servings;
	}
}
