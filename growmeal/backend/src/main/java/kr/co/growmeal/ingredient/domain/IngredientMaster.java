package kr.co.growmeal.ingredient.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredient_masters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngredientMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(name = "main_nutrient")
    private String mainNutrient;

    @Column(name = "extra_nutrient")
    private String extraNutrient;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public IngredientMaster(String name, String category, String mainNutrient, String extraNutrient, String description) {
        this.name = name;
        this.category = category;
        this.mainNutrient = mainNutrient;
        this.extraNutrient = extraNutrient;
        this.description = description;
    }
}
