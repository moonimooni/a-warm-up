package kr.co.growmeal.refrigerator.ui.dto.response;

import kr.co.growmeal.refrigerator.domain.RefrigeratorModel;

public record RefrigeratorModelResponse(
    String model,
    String name,
    String imageUrl
) {
    public static RefrigeratorModelResponse from(RefrigeratorModel entity) {
        return new RefrigeratorModelResponse(
            entity.getModel(),
            entity.getName(),
            entity.getImageUrl()
        );
    }
}
