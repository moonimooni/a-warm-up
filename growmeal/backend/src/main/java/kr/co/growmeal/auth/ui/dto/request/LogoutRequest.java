package kr.co.growmeal.auth.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
    @NotBlank(message = "Refresh token은 필수입니다.")
    String refreshToken
) {}
