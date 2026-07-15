package com.mealmate.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealmate.entity.Ingredient;
import com.mealmate.entity.Recipe;
import com.mealmate.entity.RecipeIngredient;
import com.mealmate.entity.User;
import com.mealmate.exception.AppException;
import com.mealmate.exception.NotFoundException;
import com.mealmate.repository.MealPlanEntryRepository;
import com.mealmate.repository.RecipeRepository;
import com.mealmate.repository.UserRepository;
import com.mealmate.request.dto.RecipeIngredientRequestDto;
import com.mealmate.request.dto.RecipeRequestDto;
import com.mealmate.response.dto.RecipeResponseDto;

@Service
public class RecipeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecipeService.class);

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IngredientService ingredientService;

	@Autowired
	private MealPlanEntryRepository mealPlanEntryRepository;

	@Transactional
	public RecipeResponseDto createRecipe(Long userId, RecipeRequestDto requestDto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));

		try {
			Recipe recipe = new Recipe();
			recipe.setUser(user);
			recipe.setTitle(requestDto.getTitle().trim());
			recipe.setDescription(requestDto.getDescription());
			recipe.setServings(requestDto.getServings());
			recipe.setCategory(requestDto.getCategory());
			recipe.setPreparationTime(requestDto.getPreparationTime());
			recipe.setPreparationSteps(requestDto.getPreparationSteps());

			attachIngredients(recipe, requestDto.getIngredients());

			Recipe saved = recipeRepository.save(recipe);
			LOGGER.info("Recipe created successfully, ID: {}, user ID: {}", saved.getId(), userId);
			return RecipeResponseDto.from(saved);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while creating recipe for user ID: {}, Error: {}", userId, e.getMessage(),
					e);
			throw new AppException("Recipe creation failed. Please try again.");
		}
	}

	public RecipeResponseDto getRecipeById(Long userId, Long recipeId) {
		Recipe recipe = findOwnedRecipeOrThrow(userId, recipeId);

		try {
			return RecipeResponseDto.from(recipe);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching recipe ID: {}, user ID: {}, Error: {}", recipeId, userId,
					e.getMessage(), e);
			throw new AppException("Failed to fetch recipe. Please try again.");
		}
	}

	public List<RecipeResponseDto> getAllRecipes(Long userId) {
		try {
			return recipeRepository.findByUserId(userId).stream().map(RecipeResponseDto::from)
					.collect(Collectors.toList());

		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching recipes for user ID: {}, Error: {}", userId, e.getMessage(),
					e);
			throw new AppException("Failed to fetch recipes. Please try again.");
		}
	}

	@Transactional
	public RecipeResponseDto updateRecipe(Long userId, Long recipeId, RecipeRequestDto requestDto) {
		Recipe recipe = findOwnedRecipeOrThrow(userId, recipeId);

		try {
			recipe.setTitle(requestDto.getTitle().trim());
			recipe.setDescription(requestDto.getDescription());
			recipe.setServings(requestDto.getServings());
			recipe.setCategory(requestDto.getCategory());
			recipe.setPreparationTime(requestDto.getPreparationTime());
			recipe.setPreparationSteps(requestDto.getPreparationSteps());

			recipe.getRecipeIngredients().clear();
			attachIngredients(recipe, requestDto.getIngredients());

			Recipe saved = recipeRepository.save(recipe);
			LOGGER.info("Recipe updated successfully, ID: {}, user ID: {}", saved.getId(), userId);
			return RecipeResponseDto.from(saved);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while updating recipe ID: {}, user ID: {}, Error: {}", recipeId, userId,
					e.getMessage(), e);
			throw new AppException("Recipe update failed. Please try again.");
		}
	}

	@Transactional
	public void deleteRecipe(Long userId, Long recipeId) {
		Recipe recipe = findOwnedRecipeOrThrow(userId, recipeId);

		if (mealPlanEntryRepository.existsByRecipeId(recipeId)) {
			throw new AppException("Recipe is used in a meal plan and cannot be deleted.");
		}

		try {
			recipeRepository.delete(recipe);
			LOGGER.info("Recipe deleted successfully, ID: {}, user ID: {}", recipeId, userId);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while deleting recipe ID: {}, user ID: {}, Error: {}", recipeId, userId,
					e.getMessage(), e);
			throw new AppException("Recipe deletion failed. Please try again.");
		}
	}

	private void attachIngredients(Recipe recipe, List<RecipeIngredientRequestDto> ingredientDtos) {
		List<String> names = ingredientDtos.stream().map(RecipeIngredientRequestDto::getIngredientName)
				.collect(Collectors.toList());
		Map<String, Ingredient> ingredientsByKey = ingredientService.resolveIngredients(names);

		for (RecipeIngredientRequestDto dto : ingredientDtos) {
			Ingredient ingredient = ingredientsByKey.get(ingredientService.normalizeKey(dto.getIngredientName()));

			RecipeIngredient recipeIngredient = new RecipeIngredient();
			recipeIngredient.setRecipe(recipe);
			recipeIngredient.setIngredient(ingredient);
			recipeIngredient.setAmount(dto.getAmount());
			recipeIngredient.setUnit(dto.getUnit());

			recipe.getRecipeIngredients().add(recipeIngredient);
		}
	}

	private Recipe findOwnedRecipeOrThrow(Long userId, Long recipeId) {
		return recipeRepository.findByIdAndUserId(recipeId, userId)
				.orElseThrow(() -> new NotFoundException("Recipe not found."));
	}
}