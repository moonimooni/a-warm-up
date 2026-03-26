package kr.co.growmeal.recipe.ui.dto.request;

import java.util.List;

public record CreateRecipeRequest(
    String name,
    String difficulty,
    List<StepRequest> steps,
    List<IngredientRequest> ingredients
) {
    public record StepRequest(int step, String description, String image) {
    }

    public record IngredientRequest(String name, String amount) {
    }
}
