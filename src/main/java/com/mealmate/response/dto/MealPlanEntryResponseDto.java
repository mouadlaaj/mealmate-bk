package com.mealmate.response.dto;

import java.time.LocalDate;

import com.mealmate.constants.MealType;
import com.mealmate.entity.MealPlanEntry;

public class MealPlanEntryResponseDto {

	private Long id;
	private Long mealPlanId;
	private Long recipeId;
	private String recipeTitle;
	private LocalDate plannedDate;
	private MealType mealType;
	private Integer servings;

	public MealPlanEntryResponseDto() {
	}

	public static MealPlanEntryResponseDto from(MealPlanEntry entry) {
		MealPlanEntryResponseDto dto = new MealPlanEntryResponseDto();
		dto.setId(entry.getId());
		dto.setMealPlanId(entry.getMealPlan().getId());
		dto.setRecipeId(entry.getRecipe().getId());
		dto.setRecipeTitle(entry.getRecipe().getTitle());
		dto.setPlannedDate(entry.getPlannedDate());
		dto.setMealType(entry.getMealType());
		dto.setServings(entry.getServings());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMealPlanId() {
		return mealPlanId;
	}

	public void setMealPlanId(Long mealPlanId) {
		this.mealPlanId = mealPlanId;
	}

	public Long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	public String getRecipeTitle() {
		return recipeTitle;
	}

	public void setRecipeTitle(String recipeTitle) {
		this.recipeTitle = recipeTitle;
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
