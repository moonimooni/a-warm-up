package kr.co.growmeal.inventory.ui.dto.request;

import java.time.LocalDate;

public record UpdateInventoryItemRequest(
    String compartmentId,
    LocalDate expiresAt
) {
}
