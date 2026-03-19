package kr.co.growmeal.refrigerator.ui.dto.response;

import kr.co.growmeal.refrigerator.domain.Refrigerator;

import java.time.LocalDateTime;
import java.util.List;

public record RefrigeratorDetailResponse(
    Long refrigeratorId,
    String nickname,
    String model,
    List<CompartmentResponse> compartments,
    LocalDateTime createdAt
) {
    public static RefrigeratorDetailResponse from(Refrigerator refrigerator, List<CompartmentResponse> compartments) {
        return new RefrigeratorDetailResponse(
            refrigerator.getId(),
            refrigerator.getNickname(),
            refrigerator.getRefrigeratorModel().getModel(),
            compartments,
            refrigerator.getCreatedAt()
        );
    }
}
