package kr.co.growmeal.recipe.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    List<RecipeStep> findByRecipeIdOrderByStep(Long recipeId);

    List<RecipeStep> findByRecipeIdIn(List<Long> recipeIds);
}
