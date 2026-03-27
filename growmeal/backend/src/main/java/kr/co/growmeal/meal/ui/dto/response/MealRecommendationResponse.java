package kr.co.growmeal.meal.ui.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MealRecommendationResponse(List<RecommendationGroup> recommendations) {

    public record RecommendationGroup(List<InventoryRecommendation> inventory) {}

    public record InventoryRecommendation(
        Long itemId,
        String name,
        String type,
        Long refrigeratorId,
        String compartmentId,
        List<String> nutrients,
        List<String> allergyInfo,
        LocalDateTime createdAt,
        LocalDate expiresAt
    ) {}
}
