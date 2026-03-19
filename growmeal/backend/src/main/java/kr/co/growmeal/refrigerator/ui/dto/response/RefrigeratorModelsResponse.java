package kr.co.growmeal.refrigerator.ui.dto.response;

import kr.co.growmeal.refrigerator.domain.RefrigeratorModel;

import java.util.List;

public record RefrigeratorModelsResponse(
    List<RefrigeratorModelResponse> models
) {
    public static RefrigeratorModelsResponse from(List<RefrigeratorModel> entities) {
        List<RefrigeratorModelResponse> models = entities.stream()
            .map(RefrigeratorModelResponse::from)
            .toList();
        return new RefrigeratorModelsResponse(models);
    }
}
