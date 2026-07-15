package com.mealmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mealmate.entity.ShoppingList;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

	List<ShoppingList> findByUserIdOrderByCreatedAtDesc(Long userId);

	Optional<ShoppingList> findByIdAndUserId(Long id, Long userId);

	List<ShoppingList> findByMealPlanIdAndUserId(Long mealPlanId, Long userId);
}
