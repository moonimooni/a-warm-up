package kr.co.growmeal.inventory.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryItemType type;

    @Column(name = "refrigerator_id", nullable = false)
    private Long refrigeratorId;

    @Column(name = "compartment_id", nullable = false)
    private String compartmentId;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public InventoryItem(
        String name,
        InventoryItemType type,
        Long refrigeratorId,
        String compartmentId,
        LocalDate expiresAt
    ) {
        this.name = name;
        this.type = type;
        this.refrigeratorId = refrigeratorId;
        this.compartmentId = compartmentId;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }
}
