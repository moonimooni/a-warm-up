package kr.co.growmeal.meal.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealFoodRepository extends JpaRepository<MealFood, Long> {
    List<MealFood> findByMealLogId(Long mealLogId);
    List<MealFood> findByMealLogIdIn(List<Long> mealLogIds);
    void deleteByMealLogId(Long mealLogId);
}
