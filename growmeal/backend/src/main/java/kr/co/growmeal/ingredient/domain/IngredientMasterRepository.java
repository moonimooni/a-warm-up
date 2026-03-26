package kr.co.growmeal.ingredient.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientMasterRepository extends JpaRepository<IngredientMaster, Long> {
    List<IngredientMaster> findByNameContaining(String name);

    Optional<IngredientMaster> findByName(String name);
}
