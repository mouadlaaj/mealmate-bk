package com.mealmate.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mealmate.entity.Ingredient;
import com.mealmate.response.dto.IngredientResponseDto;
import com.mealmate.service.IngredientService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("api/v1/ingredients")
@SecurityRequirement(name = "token")
public class IngredientController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IngredientController.class);

	@Autowired
	private IngredientService ingredientService;

	@GetMapping
	public ResponseEntity<List<IngredientResponseDto>> searchIngredients(
			@RequestParam(required = false, defaultValue = "") String search) {

		LOGGER.info("Searching ingredients, query: '{}'", search);
		List<Ingredient> ingredients = ingredientService.searchIngredients(search);
		List<IngredientResponseDto> response = ingredients.stream().map(IngredientResponseDto::from)
				.collect(Collectors.toList());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
