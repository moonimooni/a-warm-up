package kr.co.growmeal.meal.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByBabyIdAndCreatedAtBetween(Long babyId, LocalDateTime start, LocalDateTime end);
}
