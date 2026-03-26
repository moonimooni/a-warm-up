package kr.co.growmeal.recipe.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_steps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private int step;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String image;

    @Builder
    public RecipeStep(Long recipeId, int step, String description, String image) {
        this.recipeId = recipeId;
        this.step = step;
        this.description = description;
        this.image = image;
    }
}
