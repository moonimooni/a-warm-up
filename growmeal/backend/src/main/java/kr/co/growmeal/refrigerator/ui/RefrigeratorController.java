package kr.co.growmeal.refrigerator.ui;

import jakarta.validation.Valid;
import kr.co.growmeal.refrigerator.application.RefrigeratorService;
import kr.co.growmeal.refrigerator.ui.dto.request.CreateRefrigeratorRequest;
import kr.co.growmeal.refrigerator.ui.dto.request.UpdateRefrigeratorRequest;
import kr.co.growmeal.refrigerator.ui.dto.response.CreateRefrigeratorResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorDetailResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorsResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.UpdateRefrigeratorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refrigerators")
@RequiredArgsConstructor
public class RefrigeratorController {

    private final RefrigeratorService refrigeratorService;

    @PostMapping
    public ResponseEntity<CreateRefrigeratorResponse> createRefrigerator(
        @Valid @RequestBody CreateRefrigeratorRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        CreateRefrigeratorResponse response = refrigeratorService.createRefrigerator(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<RefrigeratorsResponse> getMyRefrigerators(
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        RefrigeratorsResponse response = refrigeratorService.getMyRefrigerators(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{refrigeratorId}")
    public ResponseEntity<RefrigeratorDetailResponse> getRefrigeratorDetail(
        @PathVariable Long refrigeratorId,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        RefrigeratorDetailResponse response = refrigeratorService.getRefrigeratorDetail(email, refrigeratorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{refrigeratorId}")
    public ResponseEntity<UpdateRefrigeratorResponse> updateRefrigerator(
        @PathVariable Long refrigeratorId,
        @Valid @RequestBody UpdateRefrigeratorRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        UpdateRefrigeratorResponse response = refrigeratorService.updateRefrigerator(email, refrigeratorId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{refrigeratorId}")
    public ResponseEntity<Void> deleteRefrigerator(
        @PathVariable Long refrigeratorId,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        refrigeratorService.deleteRefrigerator(email, refrigeratorId);
        return ResponseEntity.noContent().build();
    }
}
