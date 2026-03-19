package kr.co.growmeal.refrigerator.ui.dto.response;

import java.util.List;

public record CompartmentResponse(
    String compartmentId,
    String name,
    List<CompartmentItemResponse> items
) {
    public static CompartmentResponse of(String compartmentId, String name) {
        return new CompartmentResponse(compartmentId, name, List.of());
    }
}
