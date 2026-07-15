package com.mealmate.response.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.mealmate.entity.ShoppingList;

public class ShoppingListResponseDto {

	private Long id;
	private Long mealPlanId;
	private String mealPlanName;
	private String name;
	private LocalDateTime createdAt;
	private List<ShoppingListItemResponseDto> items;

	public ShoppingListResponseDto() {
	}

	public static ShoppingListResponseDto from(ShoppingList shoppingList) {
		ShoppingListResponseDto dto = new ShoppingListResponseDto();
		dto.setId(shoppingList.getId());
		if (shoppingList.getMealPlan() != null) {
			dto.setMealPlanId(shoppingList.getMealPlan().getId());
			dto.setMealPlanName(shoppingList.getMealPlan().getName());
		}
		dto.setName(shoppingList.getName());
		dto.setCreatedAt(shoppingList.getCreatedAt());
		dto.setItems(shoppingList.getItems().stream()
				.map(ShoppingListItemResponseDto::from)
				.collect(Collectors.toList()));
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

	public String getMealPlanName() {
		return mealPlanName;
	}

	public void setMealPlanName(String mealPlanName) {
		this.mealPlanName = mealPlanName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<ShoppingListItemResponseDto> getItems() {
		return items;
	}

	public void setItems(List<ShoppingListItemResponseDto> items) {
		this.items = items;
	}
}
