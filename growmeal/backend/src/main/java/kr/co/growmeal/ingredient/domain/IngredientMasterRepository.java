package kr.co.growmeal.ingredient.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientMasterRepository extends JpaRepository<IngredientMaster, Long> {
    List<IngredientMaster> findByNameContaining(String name);
}
