package kr.co.growmeal.refrigerator.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRefrigeratorRequest(
    @NotBlank(message = "냉장고 별명은 필수입니다")
    String nickname,

    @NotBlank(message = "냉장고 모델은 필수입니다")
    String model
) {}
