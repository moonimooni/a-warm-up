package kr.co.growmeal.recipe.ui.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeDetailResponse(
    Long recipeId,
    String name,
    String difficulty,
    List<RecipeResponse.StepResponse> steps,
    List<String> nutrients,
    List<RecipeResponse.IngredientResponse> ingredients,
    List<MissingIngredientResponse> missingIngredients,
    List<String> allergyWarnings,
    LocalDateTime createdAt
) {
    public record MissingIngredientResponse(String name, String amount) {
    }
}
