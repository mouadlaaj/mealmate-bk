package com.mealmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mealmate.entity.MealPlanEntry;

@Repository
public interface MealPlanEntryRepository extends JpaRepository<MealPlanEntry, Long> {

	List<MealPlanEntry> findByMealPlanId(Long mealPlanId);

	Optional<MealPlanEntry> findByIdAndMealPlanId(Long id, Long mealPlanId);

	boolean existsByRecipeId(Long recipeId);
}
