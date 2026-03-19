package kr.co.growmeal.refrigerator.ui.dto.response;

import java.time.LocalDateTime;

public record CompartmentItemResponse(
    Long itemId,
    String name,
    LocalDateTime createdAt,
    LocalDateTime expirationDate
) {
}
