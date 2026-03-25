package kr.co.growmeal.ingredient.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientMasterAllergyRepository extends JpaRepository<IngredientMasterAllergy, Long> {
    List<IngredientMasterAllergy> findByIngredientMasterId(Long ingredientMasterId);
    List<IngredientMasterAllergy> findByIngredientMasterIdIn(List<Long> ingredientMasterIds);
}
