package com.mealmate.entity;

import java.math.BigDecimal;

import com.mealmate.constants.Unit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_list_items")
public class ShoppingListItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "shopping_list_id", nullable = false)
	private ShoppingList shoppingList;

	@ManyToOne
	@JoinColumn(name = "ingredient_id", nullable = false)
	private Ingredient ingredient;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "purchased_amount", nullable = false)
	private BigDecimal purchasedAmount = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(name = "unit", nullable = false)
	private Unit unit;

	@Column(name = "is_completed", nullable = false)
	private boolean completed = false;

	public ShoppingListItem() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ShoppingList getShoppingList() {
		return shoppingList;
	}

	public void setShoppingList(ShoppingList shoppingList) {
		this.shoppingList = shoppingList;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
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