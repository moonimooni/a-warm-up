package kr.co.growmeal.refrigerator.ui.dto.response;

import kr.co.growmeal.refrigerator.domain.Refrigerator;

import java.time.LocalDateTime;

public record UpdateRefrigeratorResponse(
    Long refrigeratorId,
    String nickname,
    String model,
    LocalDateTime updatedAt
) {
    public static UpdateRefrigeratorResponse from(Refrigerator refrigerator) {
        return new UpdateRefrigeratorResponse(
            refrigerator.getId(),
            refrigerator.getNickname(),
            refrigerator.getRefrigeratorModel().getModel(),
            refrigerator.getUpdatedAt()
        );
    }
}
