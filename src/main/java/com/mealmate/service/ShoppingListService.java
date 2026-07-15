package com.mealmate.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mealmate.constants.Unit;
import com.mealmate.entity.Ingredient;
import com.mealmate.entity.MealPlan;
import com.mealmate.entity.MealPlanEntry;
import com.mealmate.entity.Recipe;
import com.mealmate.entity.RecipeIngredient;
import com.mealmate.entity.ShoppingList;
import com.mealmate.entity.ShoppingListItem;
import com.mealmate.exception.AppException;
import com.mealmate.exception.NotFoundException;
import com.mealmate.repository.ShoppingListItemRepository;
import com.mealmate.repository.ShoppingListRepository;
import com.mealmate.request.dto.ShoppingListItemRequestDto;
import com.mealmate.request.dto.ShoppingListRequestDto;
import com.mealmate.response.dto.ShoppingListResponseDto;

@Service
public class ShoppingListService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListService.class);

	@Autowired
	private ShoppingListRepository shoppingListRepository;

	@Autowired
	private ShoppingListItemRepository shoppingListItemRepository;

	@Autowired
	private IngredientService ingredientService;

	private static final Set<Unit> MASS_UNITS = EnumSet.of(Unit.GRAM, Unit.KILOGRAM);
	private static final Set<Unit> VOLUME_UNITS = EnumSet.of(Unit.MILLILITER, Unit.LITER, Unit.TABLESPOON,
			Unit.TEASPOON, Unit.CUP);

	private static final Set<Unit> WHOLE_COUNT_UNITS = EnumSet.of(Unit.PIECE, Unit.PINCH);

	private static final Map<Unit, BigDecimal> TO_BASE_FACTOR = Map.of(Unit.GRAM, BigDecimal.ONE, Unit.KILOGRAM,
			BigDecimal.valueOf(1000), Unit.MILLILITER, BigDecimal.ONE, Unit.LITER, BigDecimal.valueOf(1000),
			Unit.TABLESPOON, BigDecimal.valueOf(15), Unit.TEASPOON, BigDecimal.valueOf(5), Unit.CUP,
			BigDecimal.valueOf(240));

	private boolean sameUnitFamily(Unit a, Unit b) {
		if (a == b) {
			return true;
		}
		return (MASS_UNITS.contains(a) && MASS_UNITS.contains(b))
				|| (VOLUME_UNITS.contains(a) && VOLUME_UNITS.contains(b));
	}

	private BigDecimal convertAmount(BigDecimal amount, Unit from, Unit to) {
		if (from == to) {
			return amount;
		}
		return amount.multiply(TO_BASE_FACTOR.get(from)).divide(TO_BASE_FACTOR.get(to), 4, RoundingMode.HALF_UP);
	}

	private Unit baseUnitOf(Unit unit) {
		if (MASS_UNITS.contains(unit)) {
			return Unit.GRAM;
		}
		if (VOLUME_UNITS.contains(unit)) {
			return Unit.MILLILITER;
		}
		return unit;
	}

	private Unit chooseDisplayUnit(Unit baseUnit, BigDecimal totalBaseAmount) {
		BigDecimal threshold = BigDecimal.valueOf(1000);
		if (baseUnit == Unit.GRAM) {
			return totalBaseAmount.compareTo(threshold) >= 0 ? Unit.KILOGRAM : Unit.GRAM;
		}
		if (baseUnit == Unit.MILLILITER) {
			return totalBaseAmount.compareTo(threshold) >= 0 ? Unit.LITER : Unit.MILLILITER;
		}
		return baseUnit;
	}

	private BigDecimal finalizeDisplayAmount(BigDecimal amount, Unit unit) {
		if (WHOLE_COUNT_UNITS.contains(unit)) {
			return amount.setScale(0, RoundingMode.CEILING);
		}
		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	private List<ShoppingListItem> mergeSameIngredientItems(List<ShoppingListItem> items) {
		Map<String, ShoppingListItem> mergedByKey = new LinkedHashMap<>();
		List<ShoppingListItem> unresolved = new ArrayList<>();

		for (ShoppingListItem item : items) {
			if (item.getIngredient() == null) {
				unresolved.add(item);
				continue;
			}

			String key = item.getIngredient().getId() + "_" + baseUnitOf(item.getUnit()).name();
			ShoppingListItem existing = mergedByKey.get(key);

			if (existing == null) {
				mergedByKey.put(key, item);
				continue;
			}

			Unit base = baseUnitOf(item.getUnit());
			BigDecimal totalBaseAmount = convertAmount(existing.getAmount(), existing.getUnit(), base)
					.add(convertAmount(item.getAmount(), item.getUnit(), base));
			BigDecimal totalBasePurchased = convertAmount(existing.getPurchasedAmount(), existing.getUnit(), base)
					.add(convertAmount(item.getPurchasedAmount(), item.getUnit(), base));

			Unit displayUnit = chooseDisplayUnit(base, totalBaseAmount);
			BigDecimal displayAmount = finalizeDisplayAmount(convertAmount(totalBaseAmount, base, displayUnit),
					displayUnit);
			BigDecimal displayPurchased = convertAmount(totalBasePurchased, base, displayUnit);

			existing.setUnit(displayUnit);
			existing.setAmount(displayAmount);
			existing.setPurchasedAmount(displayPurchased);
			existing.setCompleted(displayPurchased.compareTo(displayAmount) >= 0);
		}

		List<ShoppingListItem> result = new ArrayList<>(mergedByKey.values());
		result.addAll(unresolved);
		return result;
	}

	@Transactional
	public void autoGenerateOrRefresh(Long userId, MealPlan mealPlan) {
		try {
			boolean hasEntries = mealPlan.getEntries() != null && !mealPlan.getEntries().isEmpty();
			Map<String, AggregatedItem> aggregated = hasEntries ? aggregateIngredients(mealPlan.getEntries())
					: new LinkedHashMap<>();

			ShoppingList existingList = mealPlan.getShoppingList();

			if (existingList == null) {
				if (aggregated.isEmpty()) {
					return;
				}

				ShoppingList shoppingList = new ShoppingList();
				shoppingList.setUser(mealPlan.getUser());
				shoppingList.setMealPlan(mealPlan);
				shoppingList.setName(resolveListName(null, mealPlan));
				appendItems(shoppingList, aggregated);

				mealPlan.setShoppingList(shoppingList);
				LOGGER.info("Shopping list auto-generated for meal plan ID: {}, user ID: {}", mealPlan.getId(), userId);
			} else {
				refreshItems(existingList, aggregated);
				LOGGER.info("Shopping list auto-refreshed, ID: {}, meal plan ID: {}, user ID: {}", existingList.getId(),
						mealPlan.getId(), userId);
			}

		} catch (NotFoundException e) {
			throw e;

		} catch (Exception e) {
			LOGGER.error(
					"Exception occurred while auto-generating shopping list for meal plan ID: {}, user ID: {}, Error: {}",
					mealPlan.getId(), userId, e.getMessage(), e);
			throw new AppException("Shopping list auto-generation failed. Please try again.");
		}
	}

	public ShoppingListResponseDto getShoppingListById(Long userId, Long shoppingListId) {
		ShoppingList shoppingList = findOwnedShoppingListOrThrow(userId, shoppingListId);

		try {
			return ShoppingListResponseDto.from(shoppingList);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching shopping list ID: {}, user ID: {}, Error: {}",
					shoppingListId, userId, e.getMessage(), e);
			throw new AppException("Failed to fetch shopping list. Please try again.");
		}
	}

	public List<ShoppingListResponseDto> getAllShoppingLists(Long userId) {
		try {
			return shoppingListRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
					.map(ShoppingListResponseDto::from).collect(Collectors.toList());

		} catch (Exception e) {
			LOGGER.error("Exception occurred while fetching shopping lists for user ID: {}, Error: {}", userId,
					e.getMessage(), e);
			throw new AppException("Failed to fetch shopping lists. Please try again.");
		}
	}

	@Transactional
	public ShoppingListResponseDto updateShoppingList(Long userId, Long shoppingListId,
			ShoppingListRequestDto requestDto) {
		ShoppingList shoppingList = findOwnedShoppingListOrThrow(userId, shoppingListId);

		try {
			if (StringUtils.hasText(requestDto.getName()))
				shoppingList.setName(requestDto.getName().trim());

			List<ShoppingListItemRequestDto> incomingItems = requestDto.getItems() == null ? List.of()
					: requestDto.getItems();

			List<String> ingredientNames = incomingItems.stream().map(ShoppingListItemRequestDto::getIngredientName)
					.collect(Collectors.toList());
			Map<String, Ingredient> resolvedIngredients = ingredientNames.isEmpty() ? Map.of()
					: ingredientService.resolveIngredients(ingredientNames);

			Map<Long, ShoppingListItem> existingById = shoppingList.getItems().stream()
					.collect(Collectors.toMap(ShoppingListItem::getId, item -> item));

			List<ShoppingListItem> updatedItems = new ArrayList<>();

			for (ShoppingListItemRequestDto itemDto : incomingItems) {
				Ingredient ingredient = resolvedIngredients
						.get(ingredientService.normalizeKey(itemDto.getIngredientName()));

				if (itemDto.getId() != null && existingById.containsKey(itemDto.getId())) {
					ShoppingListItem existingItem = existingById.get(itemDto.getId());
					Unit oldUnit = existingItem.getUnit();
					Unit newUnit = itemDto.getUnit();

					BigDecimal purchasedAmount = existingItem.getPurchasedAmount();
					if (oldUnit != newUnit) {
						purchasedAmount = sameUnitFamily(oldUnit, newUnit)
								? convertAmount(purchasedAmount, oldUnit, newUnit)
								: BigDecimal.ZERO;
					}

					existingItem.setIngredient(ingredient);
					existingItem.setAmount(itemDto.getAmount());
					existingItem.setUnit(newUnit);
					existingItem.setPurchasedAmount(purchasedAmount);
					existingItem.setCompleted(purchasedAmount.compareTo(itemDto.getAmount()) >= 0);
					updatedItems.add(existingItem);
				} else {
					ShoppingListItem newItem = new ShoppingListItem();
					newItem.setShoppingList(shoppingList);
					newItem.setIngredient(ingredient);
					newItem.setAmount(itemDto.getAmount());
					newItem.setUnit(itemDto.getUnit());
					newItem.setPurchasedAmount(BigDecimal.ZERO);
					newItem.setCompleted(false);
					updatedItems.add(newItem);
				}
			}

			shoppingList.getItems().clear();
			shoppingList.getItems().addAll(mergeSameIngredientItems(updatedItems));

			ShoppingList saved = shoppingListRepository.save(shoppingList);
			LOGGER.info("Shopping list updated successfully, ID: {}, user ID: {}", saved.getId(), userId);
			return ShoppingListResponseDto.from(saved);

		} catch (AppException | NotFoundException e) {
			throw e;

		} catch (Exception e) {
			LOGGER.error("Exception occurred while updating shopping list ID: {}, user ID: {}, Error: {}",
					shoppingListId, userId, e.getMessage(), e);
			throw new AppException("Shopping list update failed. Please try again.");
		}
	}

	@Transactional
	public void deleteShoppingList(Long userId, Long shoppingListId) {
		ShoppingList shoppingList = findOwnedShoppingListOrThrow(userId, shoppingListId);

		try {
			MealPlan mealPlan = shoppingList.getMealPlan();
			if (mealPlan != null) {
				mealPlan.setShoppingList(null);
			}

			shoppingListRepository.delete(shoppingList);

			LOGGER.info("Shopping list deleted successfully, ID: {}, user ID: {}", shoppingListId, userId);

		} catch (Exception e) {
			LOGGER.error("Exception occurred while deleting shopping list ID: {}, user ID: {}, Error: {}",
					shoppingListId, userId, e.getMessage(), e);
			throw new AppException("Shopping list deletion failed. Please try again.");
		}
	}

	@Transactional
	public ShoppingListResponseDto toggleItemCompleted(Long userId, Long shoppingListId, Long itemId) {
		ShoppingList shoppingList = findOwnedShoppingListOrThrow(userId, shoppingListId);
		ShoppingListItem item = findOwnedItemOrThrow(shoppingListId, itemId);

		try {
			boolean newCompleted = !item.isCompleted();
			item.setCompleted(newCompleted);
			item.setPurchasedAmount(newCompleted ? item.getAmount() : BigDecimal.ZERO);

			ShoppingList saved = shoppingListRepository.save(shoppingList);
			LOGGER.info("Shopping list item completion toggled, item ID: {}, shopping list ID: {}, user ID: {}", itemId,
					shoppingListId, userId);
			return ShoppingListResponseDto.from(saved);

		} catch (Exception e) {
			LOGGER.error(
					"Exception occurred while toggling item ID: {} in shopping list ID: {}, user ID: {}, Error: {}",
					itemId, shoppingListId, userId, e.getMessage(), e);
			throw new AppException("Failed to update shopping list item. Please try again.");
		}
	}

	@Transactional
	public ShoppingListResponseDto deleteItem(Long userId, Long shoppingListId, Long itemId) {
		ShoppingList shoppingList = findOwnedShoppingListOrThrow(userId, shoppingListId);
		ShoppingListItem item = findOwnedItemOrThrow(shoppingListId, itemId);

		try {
			shoppingList.getItems().remove(item);
			ShoppingList saved = shoppingListRepository.save(shoppingList);
			LOGGER.info("Shopping list item deleted successfully, item ID: {}, shopping list ID: {}, user ID: {}",
					itemId, shoppingListId, userId);
			return ShoppingListResponseDto.from(saved);

		} catch (Exception e) {
			LOGGER.error(
					"Exception occurred while deleting item ID: {} in shopping list ID: {}, user ID: {}, Error: {}",
					itemId, shoppingListId, userId, e.getMessage(), e);
			throw new AppException("Failed to delete shopping list item. Please try again.");
		}
	}

	private void appendItems(ShoppingList shoppingList, Map<String, AggregatedItem> aggregated) {
		for (AggregatedItem aggregatedItem : aggregated.values()) {
			Unit displayUnit = chooseDisplayUnit(aggregatedItem.unit, aggregatedItem.amount);
			BigDecimal displayAmount = finalizeDisplayAmount(
					convertAmount(aggregatedItem.amount, aggregatedItem.unit, displayUnit), displayUnit);

			ShoppingListItem item = new ShoppingListItem();
			item.setShoppingList(shoppingList);
			item.setIngredient(aggregatedItem.ingredient);
			item.setAmount(displayAmount);
			item.setUnit(displayUnit);
			item.setPurchasedAmount(BigDecimal.ZERO);
			item.setCompleted(false);
			shoppingList.getItems().add(item);
		}
	}

	private void refreshItems(ShoppingList shoppingList, Map<String, AggregatedItem> aggregated) {
		Map<String, ShoppingListItem> existingByKey = shoppingList.getItems().stream().collect(
				Collectors.toMap(item -> item.getIngredient().getId() + "_" + baseUnitOf(item.getUnit()).name(),
						item -> item, (first, second) -> first));

		for (Map.Entry<String, AggregatedItem> entry : aggregated.entrySet()) {
			AggregatedItem aggregatedItem = entry.getValue();
			Unit displayUnit = chooseDisplayUnit(aggregatedItem.unit, aggregatedItem.amount);
			BigDecimal displayAmount = finalizeDisplayAmount(
					convertAmount(aggregatedItem.amount, aggregatedItem.unit, displayUnit), displayUnit);

			ShoppingListItem existingItem = existingByKey.get(entry.getKey());

			if (existingItem != null) {
				BigDecimal convertedPurchased = existingItem.getUnit() == displayUnit
						? existingItem.getPurchasedAmount()
						: convertAmount(existingItem.getPurchasedAmount(), existingItem.getUnit(), displayUnit);

				existingItem.setUnit(displayUnit);
				existingItem.setAmount(displayAmount);
				existingItem.setPurchasedAmount(convertedPurchased);
				existingItem.setCompleted(convertedPurchased.compareTo(displayAmount) >= 0);
			} else {
				ShoppingListItem item = new ShoppingListItem();
				item.setShoppingList(shoppingList);
				item.setIngredient(aggregatedItem.ingredient);
				item.setAmount(displayAmount);
				item.setUnit(displayUnit);
				item.setPurchasedAmount(BigDecimal.ZERO);
				item.setCompleted(false);
				shoppingList.getItems().add(item);
			}
		}

		shoppingList.getItems().removeIf(item -> !aggregated
				.containsKey(item.getIngredient().getId() + "_" + baseUnitOf(item.getUnit()).name()));
	}

	private Map<String, AggregatedItem> aggregateIngredients(List<MealPlanEntry> entries) {
		Map<String, AggregatedItem> aggregated = new LinkedHashMap<>();

		for (MealPlanEntry entry : entries) {
			Recipe recipe = entry.getRecipe();
			BigDecimal multiplier = resolveMultiplier(recipe.getServings(), entry.getServings());

			for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
				Ingredient ingredient = recipeIngredient.getIngredient();
				Unit unit = recipeIngredient.getUnit();
				Unit base = baseUnitOf(unit);
				BigDecimal scaledAmount = recipeIngredient.getAmount().multiply(multiplier);
				BigDecimal baseAmount = convertAmount(scaledAmount, unit, base);

				String key = ingredient.getId() + "_" + base.name();

				AggregatedItem existing = aggregated.get(key);
				if (existing == null) {
					aggregated.put(key, new AggregatedItem(ingredient, base, baseAmount));
				} else {
					existing.amount = existing.amount.add(baseAmount);
				}
			}
		}

		return aggregated;
	}

	private BigDecimal resolveMultiplier(Integer recipeServings, Integer entryServings) {
		if (recipeServings == null || recipeServings <= 0 || entryServings == null) {
			return BigDecimal.ONE;
		}
		return BigDecimal.valueOf(entryServings).divide(BigDecimal.valueOf(recipeServings), 4, RoundingMode.HALF_UP);
	}

	private String resolveListName(String requestedName, MealPlan mealPlan) {
		if (requestedName != null && !requestedName.trim().isEmpty()) {
			return requestedName.trim();
		}
		return "Shopping List - " + mealPlan.getName();
	}

	private ShoppingList findOwnedShoppingListOrThrow(Long userId, Long shoppingListId) {
		return shoppingListRepository.findByIdAndUserId(shoppingListId, userId)
				.orElseThrow(() -> new NotFoundException("Shopping list not found."));
	}

	private ShoppingListItem findOwnedItemOrThrow(Long shoppingListId, Long itemId) {
		return shoppingListItemRepository.findByIdAndShoppingListId(itemId, shoppingListId)
				.orElseThrow(() -> new NotFoundException("Shopping list item not found."));
	}

	private static class AggregatedItem {
		private final Ingredient ingredient;
		private final Unit unit;
		private BigDecimal amount;

		private AggregatedItem(Ingredient ingredient, Unit unit, BigDecimal amount) {
			this.ingredient = ingredient;
			this.unit = unit;
			this.amount = amount;
		}
	}
}