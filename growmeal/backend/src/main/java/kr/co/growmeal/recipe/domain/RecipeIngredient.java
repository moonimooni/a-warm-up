package kr.co.growmeal.recipe.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_ingredients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "ingredient_master_id")
    private Long ingredientMasterId;

    private String name;

    @Column(nullable = false)
    private String amount;

    @Builder
    public RecipeIngredient(Long recipeId, Long ingredientMasterId, String name, String amount) {
        this.recipeId = recipeId;
        this.ingredientMasterId = ingredientMasterId;
        this.name = name;
        this.amount = amount;
    }
}
