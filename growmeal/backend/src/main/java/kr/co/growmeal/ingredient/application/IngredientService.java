package kr.co.growmeal.ingredient.application;

import kr.co.growmeal.ingredient.domain.IngredientMaster;
import kr.co.growmeal.ingredient.domain.IngredientMasterAllergy;
import kr.co.growmeal.ingredient.domain.IngredientMasterAllergyRepository;
import kr.co.growmeal.ingredient.domain.IngredientMasterRepository;
import kr.co.growmeal.ingredient.ui.dto.response.IngredientMasterResponse;
import kr.co.growmeal.ingredient.ui.dto.response.IngredientsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientMasterRepository ingredientMasterRepository;
    private final IngredientMasterAllergyRepository ingredientMasterAllergyRepository;

    @Transactional(readOnly = true)
    public IngredientsResponse getAllIngredients() {
        List<IngredientMaster> ingredients = ingredientMasterRepository.findAll();
        return toIngredientsResponse(ingredients);
    }

    private IngredientsResponse toIngredientsResponse(List<IngredientMaster> ingredients) {
        List<Long> ids = ingredients.stream().map(IngredientMaster::getId).toList();
        Map<Long, List<String>> allergyMap = ingredientMasterAllergyRepository
                .findByIngredientMasterIdIn(ids).stream()
                .collect(Collectors.groupingBy(
                        IngredientMasterAllergy::getIngredientMasterId,
                        Collectors.mapping(IngredientMasterAllergy::getAllergyInfo, Collectors.toList())
                ));

        List<IngredientMasterResponse> responses = ingredients.stream()
                .map(i -> IngredientMasterResponse.from(i, allergyMap.getOrDefault(i.getId(), List.of())))
                .toList();
        return new IngredientsResponse(responses);
    }
}
