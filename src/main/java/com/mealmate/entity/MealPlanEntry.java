package com.mealmate.entity;

import java.time.LocalDate;

import com.mealmate.constants.MealType;

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
@Table(name = "meal_plan_entries")
public class MealPlanEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "meal_plan_id", nullable = false)
	private MealPlan mealPlan;

	@ManyToOne
	@JoinColumn(name = "recipe_id", nullable = false)
	private Recipe recipe;

	@Column(name = "planned_date", nullable = false)
	private LocalDate plannedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "meal_type", nullable = false)
	private MealType mealType;

	@Column(name = "servings")
	private Integer servings;

	public MealPlanEntry() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MealPlan getMealPlan() {
		return mealPlan;
	}

	public void setMealPlan(MealPlan mealPlan) {
		this.mealPlan = mealPlan;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
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
