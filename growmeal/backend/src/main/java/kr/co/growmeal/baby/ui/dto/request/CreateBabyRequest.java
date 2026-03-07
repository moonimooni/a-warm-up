package kr.co.growmeal.baby.ui.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record CreateBabyRequest(

    @NotBlank(message = "아기 이름은 필수입니다")
    String name,

    @NotNull(message = "생년월일은 필수입니다")
    LocalDate birthDate,

    @Positive(message = "키는 양수여야 합니다")
    Double heightCm,

    @Positive(message = "몸무게는 양수여야 합니다")
    Double weightKg,

    List<String> allergies
) {}
