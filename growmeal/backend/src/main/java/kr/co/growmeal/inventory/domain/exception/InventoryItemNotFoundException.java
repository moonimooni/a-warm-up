package kr.co.growmeal.inventory.domain.exception;

public class InventoryItemNotFoundException extends RuntimeException {
    public InventoryItemNotFoundException() {
        super("해당 인벤토리 아이템을 찾을 수 없습니다");
    }
}
