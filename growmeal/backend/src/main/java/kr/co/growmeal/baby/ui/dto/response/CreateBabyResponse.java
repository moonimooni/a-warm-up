package kr.co.growmeal.baby.ui.dto.response;

import kr.co.growmeal.baby.domain.Baby;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record CreateBabyResponse(
    Long babyId,
    String name,
    LocalDate birthDate,
    Double heightCm,
    Double weightKg,
    List<String> allergies,
    LocalDateTime createdAt
) {
    public static CreateBabyResponse from(Baby baby) {
        List<String> allergyList = (baby.getAllergies() != null && !baby.getAllergies().isBlank())
            ? Arrays.asList(baby.getAllergies().split(","))
            : Collections.emptyList();

        return new CreateBabyResponse(
            baby.getId(),
            baby.getName(),
            baby.getBirthDate(),
            baby.getHeightCm(),
            baby.getWeightKg(),
            allergyList,
            baby.getCreatedAt()
        );
    }
}
