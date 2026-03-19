package kr.co.growmeal.refrigerator.ui.dto.response;

import kr.co.growmeal.refrigerator.domain.Refrigerator;

import java.time.LocalDateTime;

public record CreateRefrigeratorResponse(
    Long refrigeratorId,
    String nickname,
    String model,
    LocalDateTime createdAt
) {
    public static CreateRefrigeratorResponse from(Refrigerator refrigerator) {
        return new CreateRefrigeratorResponse(
            refrigerator.getId(),
            refrigerator.getNickname(),
            refrigerator.getRefrigeratorModel().getModel(),
            refrigerator.getCreatedAt()
        );
    }
}
