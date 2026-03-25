package kr.co.growmeal.ingredient.ui;

import kr.co.growmeal.common.ApiResponse;
import kr.co.growmeal.ingredient.application.IngredientService;
import kr.co.growmeal.ingredient.ui.dto.response.IngredientsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping("/master")
    public ResponseEntity<ApiResponse<IngredientsResponse>> getAllIngredients() {
        IngredientsResponse response = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
