package com.mealmate.response.dto;

import java.math.BigDecimal;

import com.mealmate.constants.Unit;
import com.mealmate.entity.ShoppingListItem;

public class ShoppingListItemResponseDto {

	private Long id;
	private Long ingredientId;
	private String ingredientName;
	private BigDecimal amount;
	private BigDecimal purchasedAmount;
	private Unit unit;
	private boolean completed;

	public ShoppingListItemResponseDto() {
	}

	public static ShoppingListItemResponseDto from(ShoppingListItem item) {
		ShoppingListItemResponseDto dto = new ShoppingListItemResponseDto();
		dto.setId(item.getId());
		dto.setIngredientId(item.getIngredient().getId());
		dto.setIngredientName(item.getIngredient().getName());
		dto.setAmount(item.getAmount());
		dto.setPurchasedAmount(item.getPurchasedAmount());
		dto.setUnit(item.getUnit());
		dto.setCompleted(item.isCompleted());
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

	public BigDecimal getPurchasedAmount() {
		return purchasedAmount;
	}

	public void setPurchasedAmount(BigDecimal purchasedAmount) {
		this.purchasedAmount = purchasedAmount;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}