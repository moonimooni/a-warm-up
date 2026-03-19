package kr.co.growmeal.refrigerator.ui.dto.response;

import kr.co.growmeal.refrigerator.domain.Refrigerator;

import java.util.List;

public record RefrigeratorsResponse(
    List<RefrigeratorResponse> refrigerators
) {
    public static RefrigeratorsResponse from(List<Refrigerator> refrigerators) {
        List<RefrigeratorResponse> responses = refrigerators.stream()
            .map(r -> RefrigeratorResponse.from(r, 0))
            .toList();
        return new RefrigeratorsResponse(responses);
    }
}
