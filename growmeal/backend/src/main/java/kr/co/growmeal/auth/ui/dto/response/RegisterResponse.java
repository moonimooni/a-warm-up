package kr.co.growmeal.auth.ui.dto.response;

public record RegisterResponse(
    String userId,
    String name,
    String role
) {}
