package kr.co.growmeal.refrigerator.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRefrigeratorRequest(
    @NotBlank String nickname
) {
}
