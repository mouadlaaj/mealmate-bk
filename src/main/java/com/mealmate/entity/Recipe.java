package com.mealmate.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipes")
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "title", nullable = false, length = 150)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "servings")
	private Integer servings;

	@Column(name = "category", length = 50)
	private String category;

	@Column(name = "preparation_time")
	private Integer preparationTime;

	@Column(name = "preparation_steps", columnDefinition = "TEXT")
	private String preparationSteps;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "recipe", cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, orphanRemoval = true)
	private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

	public Recipe() {
	}

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public List<RecipeIngredient> getRecipeIngredients() {
		return recipeIngredients;
	}

	public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
		this.recipeIngredients = recipeIngredients;
	}

}