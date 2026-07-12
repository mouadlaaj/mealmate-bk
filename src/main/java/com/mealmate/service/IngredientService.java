package com.mealmate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealmate.entity.Ingredient;
import com.mealmate.exception.AppException;
import com.mealmate.repository.IngredientRepository;

@Service
public class IngredientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IngredientService.class);

	@Autowired
	private IngredientRepository ingredientRepository;

	@Transactional
	public Map<String, Ingredient> resolveIngredients(List<String> rawNames) {
		try {
			Map<String, String> normalizedByKey = new HashMap<>();
			for (String rawName : rawNames) {
				String normalized = normalize(rawName);
				normalizedByKey.put(normalizeKey(normalized), normalized);
			}

			List<String> keys = new ArrayList<>(normalizedByKey.keySet());
			List<Ingredient> existing = keys.isEmpty() ? new ArrayList<>()
					: ingredientRepository.findByNameInIgnoreCase(keys);

			Map<String, Ingredient> resolved = new HashMap<>();
			for (Ingredient ingredient : existing) {
				resolved.put(normalizeKey(ingredient.getName()), ingredient);
			}
			List<String> missingNames = normalizedByKey.entrySet().stream()
					.filter(entry -> !resolved.containsKey(entry.getKey())).map(Map.Entry::getValue)
					.collect(Collectors.toList());

			if (!missingNames.isEmpty()) {
				for (Ingredient created : createAllSafely(missingNames)) {
					resolved.put(normalizeKey(created.getName()), created);
				}
			}

			return resolved;

		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Exception occurred while resolving ingredients {}, Error: {}", rawNames, e.getMessage(), e);
			throw new AppException("Failed to resolve ingredients. Please try again.");
		}
	}

	public String normalizeKey(String rawName) {
		return normalize(rawName).toLowerCase();
	}

	public List<Ingredient> searchIngredients(String search) {
		try {
			String query = search == null ? "" : search.trim();
			Pageable limit = PageRequest.of(0, 20);
			return ingredientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(query, limit);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while searching ingredients, query: '{}', Error: {}", search,
					e.getMessage(), e);
			throw new AppException("Failed to search ingredients. Please try again.");
		}
	}

	private String normalize(String rawName) {
		return rawName.trim().replaceAll("\\s+", " ");
	}

	private List<Ingredient> createAllSafely(List<String> names) {
		List<Ingredient> toCreate = names.stream().map(name -> {
			Ingredient ingredient = new Ingredient();
			ingredient.setName(name);
			return ingredient;
		}).collect(Collectors.toList());

		try {
			return ingredientRepository.saveAll(toCreate);
		} catch (DataIntegrityViolationException e) {
			LOGGER.error("Failed to batch-create ingredients {}: {}", names, e.getMessage(), e);
			throw new AppException("Failed to resolve ingredients: " + names);
		}
	}
}