package kr.co.growmeal.meal.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "meal_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "baby_id", nullable = false)
    private Long babyId;

    @Column(nullable = false)
    private String type;

    @Column(name = "snack_index")
    private Integer snackIndex;

    private String notes;

    private String reaction;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public MealLog(Long babyId, String type, Integer snackIndex, String notes, String reaction, Long createdByUserId) {
        this.babyId = babyId;
        this.type = type;
        this.snackIndex = snackIndex;
        this.notes = notes;
        this.reaction = reaction;
        this.createdByUserId = createdByUserId;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String type, Integer snackIndex, String notes, String reaction) {
        if (type != null) this.type = type;
        if (snackIndex != null) this.snackIndex = snackIndex;
        if (notes != null) this.notes = notes;
        if (reaction != null) this.reaction = reaction;
    }
}
