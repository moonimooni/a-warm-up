package kr.co.growmeal.recipe.ui;

import kr.co.growmeal.common.ApiResponse;
import kr.co.growmeal.recipe.application.RecipeService;
import kr.co.growmeal.recipe.ui.dto.request.CreateRecipeRequest;
import kr.co.growmeal.recipe.ui.dto.request.UpdateRecipeRequest;
import kr.co.growmeal.recipe.ui.dto.response.DeleteRecipeResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipeDetailResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipeResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(
        @RequestBody CreateRecipeRequest request,
        Authentication authentication
    ) {
        RecipeResponse response = recipeService.createRecipe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<RecipesResponse>> getRecipes(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        RecipesResponse response = recipeService.getRecipes(email);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeDetailResponse>> getRecipe(
        @PathVariable Long recipeId,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        RecipeDetailResponse response = recipeService.getRecipe(recipeId, email);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeDetailResponse>> updateRecipe(
        @PathVariable Long recipeId,
        @RequestBody UpdateRecipeRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        RecipeDetailResponse response = recipeService.updateRecipe(recipeId, request, email);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<DeleteRecipeResponse>> deleteRecipe(
        @PathVariable Long recipeId,
        Authentication authentication
    ) {
        DeleteRecipeResponse response = recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
