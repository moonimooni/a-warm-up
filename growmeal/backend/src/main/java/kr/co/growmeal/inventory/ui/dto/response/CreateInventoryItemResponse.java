package kr.co.growmeal.inventory.ui.dto.response;

import kr.co.growmeal.inventory.domain.InventoryItem;
import kr.co.growmeal.inventory.domain.InventoryItemType;

import java.time.LocalDate;
import java.util.List;

public record CreateInventoryItemResponse(
    Long itemId,
    String name,
    InventoryItemType type,
    Long refrigeratorId,
    String compartmentId,
    List<String> nutrients,
    List<String> allergyInfo,
    LocalDate expiresAt
) {
    public static CreateInventoryItemResponse from(
        InventoryItem inventoryItem,
        List<String> nutrients,
        List<String> allergyInfo
    ) {
        return new CreateInventoryItemResponse(
            inventoryItem.getId(),
            inventoryItem.getName(),
            inventoryItem.getType(),
            inventoryItem.getRefrigeratorId(),
            inventoryItem.getCompartmentId(),
            nutrients,
            allergyInfo,
            inventoryItem.getExpiresAt()
        );
    }
}
