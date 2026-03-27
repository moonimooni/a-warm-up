package kr.co.growmeal.meal.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meal_foods")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meal_log_id", nullable = false)
    private Long mealLogId;

    @Column(name = "inventory_item_id")
    private Long inventoryItemId;

    private String name;

    @Builder
    public MealFood(Long mealLogId, Long inventoryItemId, String name) {
        this.mealLogId = mealLogId;
        this.inventoryItemId = inventoryItemId;
        this.name = name;
    }
}
