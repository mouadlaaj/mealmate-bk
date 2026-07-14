package com.mealmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mealmate.entity.MealPlan;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

	List<MealPlan> findByUserId(Long userId);

	Optional<MealPlan> findByIdAndUserId(Long id, Long userId);
}
