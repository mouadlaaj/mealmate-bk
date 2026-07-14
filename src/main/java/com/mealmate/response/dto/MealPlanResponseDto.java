package com.mealmate.response.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.mealmate.entity.MealPlan;

public class MealPlanResponseDto {

	private Long id;
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	private List<MealPlanEntryResponseDto> entries;

	public MealPlanResponseDto() {
	}

	public static MealPlanResponseDto from(MealPlan mealPlan) {
		MealPlanResponseDto dto = new MealPlanResponseDto();
		dto.setId(mealPlan.getId());
		dto.setName(mealPlan.getName());
		dto.setStartDate(mealPlan.getStartDate());
		dto.setEndDate(mealPlan.getEndDate());
		dto.setEntries(mealPlan.getEntries().stream()
				.map(MealPlanEntryResponseDto::from)
				.collect(Collectors.toList()));
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public List<MealPlanEntryResponseDto> getEntries() {
		return entries;
	}

	public void setEntries(List<MealPlanEntryResponseDto> entries) {
		this.entries = entries;
	}
}
