package kr.co.growmeal.inventory.ui.dto.response;

import java.util.List;

public record InventoryResponse(
    List<InventoryItemResponse> inventory
) {
}
