package com.mealmate.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealmate.request.dto.ShoppingListRequestDto;
import com.mealmate.response.dto.ShoppingListResponseDto;
import com.mealmate.security.service.UserDetailsImpl;
import com.mealmate.service.ShoppingListService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/shopping-lists")
@SecurityRequirement(name = "token")
public class ShoppingListController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListController.class);

	@Autowired
	private ShoppingListService shoppingListService;

	@GetMapping("/{shoppingListId}")
	public ResponseEntity<ShoppingListResponseDto> getShoppingList(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "shoppingListId") Long shoppingListId) {

		LOGGER.info("Fetching shopping list ID: {} for user ID: {}", shoppingListId, currentUser.getId());
		ShoppingListResponseDto shoppingList = shoppingListService.getShoppingListById(currentUser.getId(),
				shoppingListId);
		return new ResponseEntity<>(shoppingList, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<ShoppingListResponseDto>> getAllShoppingLists(
			@AuthenticationPrincipal UserDetailsImpl currentUser) {

		LOGGER.info("Fetching all shopping lists for user ID: {}", currentUser.getId());
		List<ShoppingListResponseDto> shoppingLists = shoppingListService.getAllShoppingLists(currentUser.getId());
		return new ResponseEntity<>(shoppingLists, HttpStatus.OK);
	}

	@PutMapping("/{shoppingListId}")
	public ResponseEntity<ShoppingListResponseDto> updateShoppingList(
			@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "shoppingListId") Long shoppingListId,
			@Valid @RequestBody ShoppingListRequestDto requestDto) {

		LOGGER.info("Updating shopping list ID: {} for user ID: {}", shoppingListId, currentUser.getId());
		ShoppingListResponseDto updated = shoppingListService.updateShoppingList(currentUser.getId(), shoppingListId,
				requestDto);
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	@DeleteMapping("/{shoppingListId}")
	public ResponseEntity<Void> deleteShoppingList(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "shoppingListId") Long shoppingListId) {

		LOGGER.info("Deleting shopping list ID: {} for user ID: {}", shoppingListId, currentUser.getId());
		shoppingListService.deleteShoppingList(currentUser.getId(), shoppingListId);
		LOGGER.info("Shopping list deleted successfully, ID: {}", shoppingListId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{shoppingListId}/items/{itemId}/toggle-complete")
	public ResponseEntity<ShoppingListResponseDto> toggleItemCompleted(
			@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "shoppingListId") Long shoppingListId, @PathVariable(name = "itemId") Long itemId) {

		LOGGER.info("Toggling completion for item ID: {} in shopping list ID: {} for user ID: {}", itemId,
				shoppingListId, currentUser.getId());
		ShoppingListResponseDto updated = shoppingListService.toggleItemCompleted(currentUser.getId(), shoppingListId,
				itemId);
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	@DeleteMapping("/{shoppingListId}/items/{itemId}")
	public ResponseEntity<ShoppingListResponseDto> deleteItem(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "shoppingListId") Long shoppingListId, @PathVariable(name = "itemId") Long itemId) {

		LOGGER.info("Deleting item ID: {} in shopping list ID: {} for user ID: {}", itemId, shoppingListId,
				currentUser.getId());
		ShoppingListResponseDto updated = shoppingListService.deleteItem(currentUser.getId(), shoppingListId, itemId);
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

}