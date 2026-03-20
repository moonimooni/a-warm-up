package kr.co.growmeal.inventory.ui.dto.response;

import kr.co.growmeal.inventory.domain.InventoryItem;
import kr.co.growmeal.inventory.domain.InventoryItemType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InventoryItemResponse(
    Long itemId,
    String name,
    InventoryItemType type,
    Long refrigeratorId,
    String compartmentId,
    List<String> nutrients,
    List<String> allergyInfo,
    LocalDate expiresAt,
    LocalDateTime createdAt
) {
    public static InventoryItemResponse from(
        InventoryItem inventoryItem,
        List<String> nutrients,
        List<String> allergyInfo
    ) {
        return new InventoryItemResponse(
            inventoryItem.getId(),
            inventoryItem.getName(),
            inventoryItem.getType(),
            inventoryItem.getRefrigeratorId(),
            inventoryItem.getCompartmentId(),
            nutrients,
            allergyInfo,
            inventoryItem.getExpiresAt(),
            inventoryItem.getCreatedAt()
        );
    }
}
