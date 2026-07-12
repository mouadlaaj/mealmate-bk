package com.mealmate.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mealmate.entity.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

	@Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) IN :names")
	List<Ingredient> findByNameInIgnoreCase(@Param("names") List<String> names);

	List<Ingredient> findByNameContainingIgnoreCaseOrderByNameAsc(String search, Pageable pageable);
}