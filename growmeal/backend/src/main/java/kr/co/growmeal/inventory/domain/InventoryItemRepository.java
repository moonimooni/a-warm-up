package kr.co.growmeal.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByRefrigeratorIdIn(List<Long> refrigeratorIds);
}
