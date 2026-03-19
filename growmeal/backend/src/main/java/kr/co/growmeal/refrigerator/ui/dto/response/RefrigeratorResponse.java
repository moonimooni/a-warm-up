package kr.co.growmeal.refrigerator.ui.dto.response;

import kr.co.growmeal.refrigerator.domain.Refrigerator;

import java.time.LocalDateTime;

public record RefrigeratorResponse(
    Long refrigeratorId,
    String nickname,
    String model,
    int itemCount,
    LocalDateTime createdAt
) {
    public static RefrigeratorResponse from(Refrigerator refrigerator, int itemCount) {
        return new RefrigeratorResponse(
            refrigerator.getId(),
            refrigerator.getNickname(),
            refrigerator.getRefrigeratorModel().getModel(),
            itemCount,
            refrigerator.getCreatedAt()
        );
    }
}
