package kr.co.growmeal.meal.ui.dto.request;

import java.util.List;

public record UpdateMealRequest(
    String type,
    Integer snackIndex,
    List<FoodRequest> foods,
    String notes,
    String reaction
) {
    public record FoodRequest(String name, Long inventoryItemId) {}
}
