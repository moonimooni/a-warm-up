package kr.co.growmeal.refrigerator.ui;

import jakarta.validation.Valid;
import kr.co.growmeal.refrigerator.application.RefrigeratorService;
import kr.co.growmeal.refrigerator.ui.dto.request.CreateRefrigeratorRequest;
import kr.co.growmeal.refrigerator.ui.dto.response.CreateRefrigeratorResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorsResponse;
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
}
