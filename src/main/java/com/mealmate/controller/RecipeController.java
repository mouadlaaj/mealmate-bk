package com.mealmate.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealmate.request.dto.RecipeRequestDto;
import com.mealmate.response.dto.GenericMessage;
import com.mealmate.response.dto.RecipeResponseDto;
import com.mealmate.security.service.UserDetailsImpl;
import com.mealmate.service.RecipeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/recipes")
@SecurityRequirement(name = "token")
public class RecipeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecipeController.class);

	@Autowired
	private RecipeService recipeService;

	@PostMapping
	public ResponseEntity<RecipeResponseDto> createRecipe(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@Valid @RequestBody RecipeRequestDto requestDto) {

		LOGGER.info("Creating recipe for user ID: {}", currentUser.getId());
		RecipeResponseDto created = recipeService.createRecipe(currentUser.getId(), requestDto);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@GetMapping("/{recipeId}")
	public ResponseEntity<RecipeResponseDto> getRecipe(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "recipeId") Long recipeId) {

		LOGGER.info("Fetching recipe ID: {} for user ID: {}", recipeId, currentUser.getId());
		RecipeResponseDto recipe = recipeService.getRecipeById(currentUser.getId(), recipeId);
		return new ResponseEntity<>(recipe, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<RecipeResponseDto>> getAllRecipes(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		LOGGER.info("Fetching all recipes for user ID: {}", currentUser.getId());
		List<RecipeResponseDto> recipes = recipeService.getAllRecipes(currentUser.getId());
		return new ResponseEntity<>(recipes, HttpStatus.OK);
	}

	@PutMapping("/{recipeId}")
	public ResponseEntity<RecipeResponseDto> updateRecipe(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "recipeId") Long recipeId, @Valid @RequestBody RecipeRequestDto requestDto) {

		LOGGER.info("Updating recipe ID: {} for user ID: {}", recipeId, currentUser.getId());
		RecipeResponseDto updated = recipeService.updateRecipe(currentUser.getId(), recipeId, requestDto);
		LOGGER.info("Recipe updated successfully, ID: {}", recipeId);
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	@DeleteMapping("/{recipeId}")
	public ResponseEntity<GenericMessage> deleteRecipe(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "recipeId") Long recipeId) {

		LOGGER.info("Deleting recipe ID: {} for user ID: {}", recipeId, currentUser.getId());
		recipeService.deleteRecipe(currentUser.getId(), recipeId);

		GenericMessage message = new GenericMessage();
		message.setMessage("Recipe deleted successfully");
		message.setTime(LocalDateTime.now());
		LOGGER.info("Recipe deleted successfully, ID: {}", recipeId);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
}
