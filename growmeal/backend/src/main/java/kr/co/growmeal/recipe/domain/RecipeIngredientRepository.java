package kr.co.growmeal.recipe.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    List<RecipeIngredient> findByRecipeId(Long recipeId);

    List<RecipeIngredient> findByRecipeIdIn(List<Long> recipeIds);
}
