package kr.co.growmeal.ingredient.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredient_master_allergies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngredientMasterAllergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient_master_id", nullable = false)
    private Long ingredientMasterId;

    @Column(name = "allergy_info", nullable = false)
    private String allergyInfo;

    @Builder
    public IngredientMasterAllergy(Long ingredientMasterId, String allergyInfo) {
        this.ingredientMasterId = ingredientMasterId;
        this.allergyInfo = allergyInfo;
    }
}
