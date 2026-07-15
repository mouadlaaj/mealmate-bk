package com.mealmate.request.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ShoppingListRequestDto {

	//@NotBlank(message = "Name is required")
	@Size(max = 100)
	private String name;

	@NotNull(message = "Items are required")
	@Valid
	private List<ShoppingListItemRequestDto> items = new ArrayList<>();

	public ShoppingListRequestDto() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ShoppingListItemRequestDto> getItems() {
		return items;
	}

	public void setItems(List<ShoppingListItemRequestDto> items) {
		this.items = items;
	}
}