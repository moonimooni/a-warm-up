package kr.co.growmeal.inventory.ui;

import jakarta.validation.Valid;
import kr.co.growmeal.inventory.application.InventoryService;
import kr.co.growmeal.inventory.ui.dto.request.CreateInventoryItemRequest;
import kr.co.growmeal.inventory.ui.dto.response.CreateInventoryItemResponse;
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
    public ResponseEntity<InventoryResponse> getInventory(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        InventoryResponse response = inventoryService.getInventory(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateInventoryItemResponse> createInventory(
        @Valid @RequestBody CreateInventoryItemRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        CreateInventoryItemResponse response = inventoryService.createInventory(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
