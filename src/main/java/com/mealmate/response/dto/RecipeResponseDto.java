package com.mealmate.response.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.mealmate.entity.Recipe;

public class RecipeResponseDto {

	private Long id;
	private String title;
	private String description;
	private Integer servings;
	private String category;
	private Integer preparationTime;
	private String preparationSteps;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<RecipeIngredientResponseDto> ingredients;

	public RecipeResponseDto() {
	}

	public static RecipeResponseDto from(Recipe recipe) {
		RecipeResponseDto dto = new RecipeResponseDto();
		dto.setId(recipe.getId());
		dto.setTitle(recipe.getTitle());
		dto.setDescription(recipe.getDescription());
		dto.setServings(recipe.getServings());
		dto.setCategory(recipe.getCategory());
		dto.setPreparationTime(recipe.getPreparationTime());
		dto.setPreparationSteps(recipe.getPreparationSteps());
		dto.setCreatedAt(recipe.getCreatedAt());
		dto.setUpdatedAt(recipe.getUpdatedAt());
		dto.setIngredients(recipe.getRecipeIngredients().stream()
				.map(RecipeIngredientResponseDto::from)
				.collect(Collectors.toList()));
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<RecipeIngredientResponseDto> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeIngredientResponseDto> ingredients) {
		this.ingredients = ingredients;
	}
}
