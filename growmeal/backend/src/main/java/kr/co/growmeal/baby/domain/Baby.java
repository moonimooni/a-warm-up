package kr.co.growmeal.baby.domain;

import jakarta.persistence.*;
import kr.co.growmeal.auth.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "babies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Baby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "allergies")
    private String allergies;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Baby(User user, String name, LocalDate birthDate,
            Double heightCm, Double weightKg, String allergies) {
        this.user = user;
        this.name = name;
        this.birthDate = birthDate;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.allergies = allergies;
        this.createdAt = LocalDateTime.now();
    }
}
