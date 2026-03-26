package kr.co.growmeal.recipe.ui.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeResponse(
    Long recipeId,
    String name,
    String difficulty,
    List<StepResponse> steps,
    List<String> nutrients,
    List<IngredientResponse> ingredients,
    List<String> allergyWarnings,
    LocalDateTime createdAt
) {
    public record StepResponse(int step, String description, String image) {
    }

    public record IngredientResponse(String name, String amount) {
    }
}
