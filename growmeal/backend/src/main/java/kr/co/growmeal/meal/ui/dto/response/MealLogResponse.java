package kr.co.growmeal.meal.ui.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record MealLogResponse(
    Long mealId,
    String type,
    Integer snackIndex,
    List<FoodResponse> foods,
    String notes,
    String reaction,
    String createdBy,
    LocalDateTime createdAt
) {
    public record FoodResponse(String name, Long inventoryItemId) {}
}
