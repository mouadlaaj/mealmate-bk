package com.mealmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mealmate.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	List<Recipe> findByUserId(Long userId);

	Optional<Recipe> findByIdAndUserId(Long id, Long userId);

	List<Recipe> findByIdInAndUserId(List<Long> ids, Long userId);
}
