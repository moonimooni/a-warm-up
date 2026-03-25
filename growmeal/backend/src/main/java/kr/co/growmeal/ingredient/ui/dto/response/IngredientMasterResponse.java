package kr.co.growmeal.ingredient.ui.dto.response;

import kr.co.growmeal.ingredient.domain.IngredientMaster;

import java.util.ArrayList;
import java.util.List;

public record IngredientMasterResponse(
    Long ingredientId,
    String name,
    String category,
    List<String> nutrients,
    List<String> allergyInfo,
    String description
) {
    public static IngredientMasterResponse from(IngredientMaster entity, List<String> allergyInfo) {
        List<String> nutrients = new ArrayList<>();
        if (entity.getMainNutrient() != null) {
            nutrients.add(entity.getMainNutrient());
        }
        if (entity.getExtraNutrient() != null) {
            nutrients.add(entity.getExtraNutrient());
        }
        return new IngredientMasterResponse(
            entity.getId(),
            entity.getName(),
            entity.getCategory(),
            nutrients,
            allergyInfo,
            entity.getDescription()
        );
    }
}
