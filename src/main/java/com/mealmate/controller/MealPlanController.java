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

import com.mealmate.request.dto.MealPlanRequestDto;
import com.mealmate.response.dto.GenericMessage;
import com.mealmate.response.dto.MealPlanResponseDto;
import com.mealmate.security.service.UserDetailsImpl;
import com.mealmate.service.MealPlanService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/meal-plans")
@SecurityRequirement(name = "token")
public class MealPlanController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MealPlanController.class);

	@Autowired
	private MealPlanService mealPlanService;

	@PostMapping
	public ResponseEntity<MealPlanResponseDto> createMealPlan(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@Valid @RequestBody MealPlanRequestDto requestDto) {

		LOGGER.info("Creating meal plan for user ID: {}", currentUser.getId());
		MealPlanResponseDto created = mealPlanService.createMealPlan(currentUser.getId(), requestDto);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@GetMapping("/{mealPlanId}")
	public ResponseEntity<MealPlanResponseDto> getMealPlan(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "mealPlanId") Long mealPlanId) {

		LOGGER.info("Fetching meal plan ID: {} for user ID: {}", mealPlanId, currentUser.getId());
		MealPlanResponseDto mealPlan = mealPlanService.getMealPlanById(currentUser.getId(), mealPlanId);
		return new ResponseEntity<>(mealPlan, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<MealPlanResponseDto>> getAllMealPlans(
			@AuthenticationPrincipal UserDetailsImpl currentUser) {

		LOGGER.info("Fetching all meal plans for user ID: {}", currentUser.getId());
		List<MealPlanResponseDto> mealPlans = mealPlanService.getAllMealPlans(currentUser.getId());
		return new ResponseEntity<>(mealPlans, HttpStatus.OK);
	}

	@PutMapping("/{mealPlanId}")
	public ResponseEntity<MealPlanResponseDto> updateMealPlan(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "mealPlanId") Long mealPlanId, @Valid @RequestBody MealPlanRequestDto requestDto) {

		LOGGER.info("Updating meal plan ID: {} for user ID: {}", mealPlanId, currentUser.getId());
		MealPlanResponseDto updated = mealPlanService.updateMealPlan(currentUser.getId(), mealPlanId, requestDto);
		LOGGER.info("Meal plan updated successfully, ID: {}", mealPlanId);
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	@DeleteMapping("/{mealPlanId}")
	public ResponseEntity<GenericMessage> deleteMealPlan(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable(name = "mealPlanId") Long mealPlanId) {

		LOGGER.info("Deleting meal plan ID: {} for user ID: {}", mealPlanId, currentUser.getId());
		mealPlanService.deleteMealPlan(currentUser.getId(), mealPlanId);

		GenericMessage message = new GenericMessage();
		message.setMessage("Meal plan deleted successfully");
		message.setTime(LocalDateTime.now());
		LOGGER.info("Meal plan deleted successfully, ID: {}", mealPlanId);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

}
