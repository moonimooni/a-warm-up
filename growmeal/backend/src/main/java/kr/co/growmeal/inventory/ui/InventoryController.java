package kr.co.growmeal.inventory.ui;

import jakarta.validation.Valid;
import kr.co.growmeal.common.ApiResponse;
import kr.co.growmeal.inventory.application.InventoryService;
import kr.co.growmeal.inventory.ui.dto.request.CreateInventoryItemRequest;
import kr.co.growmeal.inventory.ui.dto.request.UpdateInventoryItemRequest;
import kr.co.growmeal.inventory.ui.dto.response.CreateInventoryItemResponse;
import kr.co.growmeal.inventory.ui.dto.response.InventoryItemResponse;
import kr.co.growmeal.inventory.ui.dto.response.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        InventoryResponse response = inventoryService.getInventory(email);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateInventoryItemResponse>> createInventory(
        @Valid @RequestBody CreateInventoryItemRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        CreateInventoryItemResponse response = inventoryService.createInventory(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> updateInventory(
        @PathVariable Long itemId,
        @RequestBody UpdateInventoryItemRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        InventoryItemResponse response = inventoryService.updateInventory(email, itemId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
