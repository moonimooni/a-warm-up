package kr.co.growmeal.auth.ui.dto.response;

public record MeResponse(
    String userId,
    String name,
    String role,
    String babyId
) {}
