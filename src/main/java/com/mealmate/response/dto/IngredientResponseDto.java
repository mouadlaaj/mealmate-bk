package com.mealmate.response.dto;

import com.mealmate.entity.Ingredient;

public class IngredientResponseDto {

	private Long id;
	private String name;

	public IngredientResponseDto() {
	}

	public static IngredientResponseDto from(Ingredient ingredient) {
		IngredientResponseDto dto = new IngredientResponseDto();
		dto.setId(ingredient.getId());
		dto.setName(ingredient.getName());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
