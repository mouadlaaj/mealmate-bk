package com.mealmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mealmate.entity.ShoppingListItem;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

	Optional<ShoppingListItem> findByIdAndShoppingListId(Long id, Long shoppingListId);
}
