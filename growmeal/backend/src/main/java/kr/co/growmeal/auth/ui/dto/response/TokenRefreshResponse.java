package kr.co.growmeal.auth.ui.dto.response;

public record TokenRefreshResponse(
    String accessToken,
    int expiresIn
) {}
