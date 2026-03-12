package kr.co.growmeal.auth.ui.dto.response;

public record LoginResponse(
    String userId,
    String name,
    String role,
    String accessToken,
    String refreshToken,
    int expiresIn  // Access Token 만료 시간 (초) - 15분 = 900초
) {}
