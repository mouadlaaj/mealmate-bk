package com.mealmate.request.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class RecipeRequestDto {

	@NotBlank(message = "Title is required")
	@Size(max = 150)
	private String title;

	private String description;

	@Positive(message = "Servings must be greater than 0")
	private Integer servings;

	@Size(max = 50)
	private String category;

	@Positive(message = "Preparation time must be greater than 0")
	private Integer preparationTime;

	private String preparationSteps;

	@NotEmpty(message = "At least one ingredient is required")
	@Valid
	private List<RecipeIngredientRequestDto> ingredients;

	public RecipeRequestDto() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getServings() {
		return servings;
	}

	public void setServings(Integer servings) {
		this.servings = servings;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(Integer preparationTime) {
		this.preparationTime = preparationTime;
	}

	public String getPreparationSteps() {
		return preparationSteps;
	}

	public void setPreparationSteps(String preparationSteps) {
		this.preparationSteps = preparationSteps;
	}

	public List<RecipeIngredientRequestDto> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeIngredientRequestDto> ingredients) {
		this.ingredients = ingredients;
	}
}
