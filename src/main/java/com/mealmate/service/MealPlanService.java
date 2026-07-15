package com.mealmate.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealmate.entity.MealPlan;
import com.mealmate.entity.MealPlanEntry;
import com.mealmate.entity.Recipe;
import com.mealmate.entity.User;
import com.mealmate.exception.AppException;
import com.mealmate.exception.NotFoundException;
import com.mealmate.repository.MealPlanRepository;
import com.mealmate.repository.RecipeRepository;
import com.mealmate.repository.UserRepository;
import com.mealmate.request.dto.MealPlanEntryRequestDto;
import com.mealmate.request.dto.MealPlanRequestDto;
import com.mealmate.response.dto.MealPlanResponseDto;

@Service
public class MealPlanService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MealPlanService.class);

	@Autowired
	private MealPlanRepository mealPlanRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private ShoppingListService shoppingListService;

	@Transactional
	public MealPlanResponseDto createMealPlan(Long userId, MealPlanRequestDto requestDto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));

		validateDateRange(requestDto.getStartDate(), requestDto.getEndDate());

		try {
			MealPlan mealPlan = new MealPlan();
			mealPlan.setUser(user);
			mealPlan.setName(requestDto.getName().trim());
			mealPlan.setStartDate(requestDto.getStartDate());
			mealPlan.setEndDate(requestDto.getEndDate());

			if (requestDto.getEntries() != null && !requestDto.getEntries().isEmpty()) {
				mealPlan.getEntries().addAll(buildEntries(mealPlan, userId, requestDto.getEntries()));
			}
			shoppingListService.autoGenerateOrRefresh(userId, mealPlan);
			MealPlan saved = mealPlanRepository.save(mealPlan);

			LOGGER.info("Meal plan created successfully, ID: {}, user ID: {}", saved.getId(), userId);
			return MealPlanResponseDto.from(saved);

		} catch (AppException | NotFoundException e) {
			throw e;

		} catch (Exception e) {
			LOGGER.error("Exception occurred while creating meal plan for user ID: {}, Error: {}", userId,
					e.getMessage(), e);
			throw new AppException("Meal plan creation failed. Please try again.");
		}
	}

	public MealPlanResponseDto getMealPlanById(Long userId, Long mealPlanId) {
		MealPlan mealPlan = findOwnedMealPlanOrThrow(userId, mealPlanId);

		try {
			return MealPlanResponseDto.from(mealPlan);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching meal plan ID: {}, user ID: {}, Error: {}", mealPlanId,
					userId, e.getMessage(), e);
			throw new AppException("Failed to fetch meal plan. Please try again.");
		}
	}

	public List<MealPlanResponseDto> getAllMealPlans(Long userId) {
		try {
			return mealPlanRepository.findByUserId(userId).stream().map(MealPlanResponseDto::from)
					.collect(Collectors.toList());

		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching meal plans for user ID: {}, Error: {}", userId,
					e.getMessage(), e);
			throw new AppException("Failed to fetch meal plans. Please try again.");
		}
	}

	@Transactional
	public MealPlanResponseDto updateMealPlan(Long userId, Long mealPlanId, MealPlanRequestDto requestDto) {
		MealPlan mealPlan = findOwnedMealPlanOrThrow(userId, mealPlanId);

		validateDateRange(requestDto.getStartDate(), requestDto.getEndDate());

		try {
			mealPlan.setName(requestDto.getName().trim());
			mealPlan.setStartDate(requestDto.getStartDate());
			mealPlan.setEndDate(requestDto.getEndDate());

			if (requestDto.getEntries() != null) {
				mealPlan.getEntries().clear();
				if (!requestDto.getEntries().isEmpty()) {
					mealPlan.getEntries().addAll(buildEntries(mealPlan, userId, requestDto.getEntries()));
				}
			}
			shoppingListService.autoGenerateOrRefresh(userId, mealPlan);
			MealPlan saved = mealPlanRepository.save(mealPlan);

			LOGGER.info("Meal plan updated successfully, ID: {}, user ID: {}", saved.getId(), userId);
			return MealPlanResponseDto.from(saved);

		} catch (AppException | NotFoundException e) {
			throw e;

		} catch (Exception e) {
			LOGGER.error("Exception occurred while updating meal plan ID: {}, user ID: {}, Error: {}", mealPlanId,
					userId, e.getMessage(), e);
			throw new AppException("Meal plan update failed. Please try again.");
		}
	}

	@Transactional
	public void deleteMealPlan(Long userId, Long mealPlanId) {
		MealPlan mealPlan = findOwnedMealPlanOrThrow(userId, mealPlanId);

		try {
			mealPlanRepository.delete(mealPlan);
			LOGGER.info("Meal plan deleted successfully, ID: {}, user ID: {}", mealPlanId, userId);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while deleting meal plan ID: {}, user ID: {}, Error: {}", mealPlanId,
					userId, e.getMessage(), e);
			throw new AppException("Meal plan deletion failed. Please try again.");
		}
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		LocalDate today = LocalDate.now();

		if (startDate.isBefore(today)) {
			throw new AppException("Start date cannot be in the past.");
		}
		if (endDate.isBefore(startDate)) {
			throw new AppException("End date cannot be before start date.");
		}
	}

	private void validateEntryDateWithinPlan(MealPlan mealPlan, LocalDate plannedDate) {
		LocalDate today = LocalDate.now();

		if (plannedDate.isBefore(today)) {
			throw new AppException("Planned date cannot be in the past.");
		}
		if (plannedDate.isBefore(mealPlan.getStartDate()) || plannedDate.isAfter(mealPlan.getEndDate())) {
			throw new AppException("Planned date must fall within the meal plan's date range.");
		}
	}

	private MealPlan findOwnedMealPlanOrThrow(Long userId, Long mealPlanId) {
		return mealPlanRepository.findByIdAndUserId(mealPlanId, userId)
				.orElseThrow(() -> new NotFoundException("Meal plan not found."));
	}

	private List<MealPlanEntry> buildEntries(MealPlan mealPlan, Long userId, List<MealPlanEntryRequestDto> entryDtos) {
		List<Long> recipeIds = entryDtos.stream().map(MealPlanEntryRequestDto::getRecipeId).distinct()
				.collect(Collectors.toList());

		Map<Long, Recipe> recipesById = recipeRepository.findByIdInAndUserId(recipeIds, userId).stream()
				.collect(Collectors.toMap(Recipe::getId, recipe -> recipe));

		return entryDtos.stream().map(dto -> {
			Recipe recipe = recipesById.get(dto.getRecipeId());
			if (recipe == null) {
				throw new NotFoundException("Recipe not found.");
			}
			validateEntryDateWithinPlan(mealPlan, dto.getPlannedDate());

			MealPlanEntry entry = new MealPlanEntry();
			entry.setMealPlan(mealPlan);
			entry.setRecipe(recipe);
			entry.setPlannedDate(dto.getPlannedDate());
			entry.setMealType(dto.getMealType());
			entry.setServings(dto.getServings());
			return entry;
		}).collect(Collectors.toList());
	}
}
