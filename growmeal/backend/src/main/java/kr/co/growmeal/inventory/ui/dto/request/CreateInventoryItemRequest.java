package kr.co.growmeal.inventory.ui.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.growmeal.inventory.domain.InventoryItemType;

import java.time.LocalDate;

public record CreateInventoryItemRequest(
    @NotBlank(message = "인벤토리 이름은 필수입니다")
    String name,

    @NotNull(message = "인벤토리 타입은 필수입니다")
    InventoryItemType type,

    @NotNull(message = "냉장고 ID는 필수입니다")
    Long refrigeratorId,

    @NotBlank(message = "냉장고 칸 ID는 필수입니다")
    String compartmentId,

    @NotNull(message = "유통기한은 필수입니다")
    LocalDate expiresAt
) {
}
