package kr.co.growmeal.meal.ui.dto.response;

import java.util.List;

public record MealLogsResponse(String date, List<MealLogResponse> meals) {}
