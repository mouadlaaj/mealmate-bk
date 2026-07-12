package com.mealmate.response.dto;

import java.math.BigDecimal;

import com.mealmate.constants.Unit;
import com.mealmate.entity.RecipeIngredient;

public class RecipeIngredientResponseDto {

	private Long id;
	private Long ingredientId;
	private String ingredientName;
	private BigDecimal amount;
	private Unit unit;

	public RecipeIngredientResponseDto() {
	}

	public static RecipeIngredientResponseDto from(RecipeIngredient ri) {
		RecipeIngredientResponseDto dto = new RecipeIngredientResponseDto();
		dto.setId(ri.getId());
		dto.setIngredientId(ri.getIngredient().getId());
		dto.setIngredientName(ri.getIngredient().getName());
		dto.setAmount(ri.getAmount());
		dto.setUnit(ri.getUnit());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(Long ingredientId) {
		this.ingredientId = ingredientId;
	}

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
}
