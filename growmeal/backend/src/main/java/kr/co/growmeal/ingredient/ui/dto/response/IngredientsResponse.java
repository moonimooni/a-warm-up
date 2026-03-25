package kr.co.growmeal.ingredient.ui.dto.response;

import java.util.List;

public record IngredientsResponse(
    List<IngredientMasterResponse> ingredients
) {
}
