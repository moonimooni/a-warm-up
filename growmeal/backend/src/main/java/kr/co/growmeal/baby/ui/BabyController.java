package kr.co.growmeal.baby.ui;

import jakarta.validation.Valid;
import kr.co.growmeal.baby.application.BabyService;
import kr.co.growmeal.baby.ui.dto.request.CreateBabyRequest;
import kr.co.growmeal.baby.ui.dto.response.CreateBabyResponse;
import kr.co.growmeal.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/babies")
@RequiredArgsConstructor
public class BabyController {

    private final BabyService babyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateBabyResponse>> createBaby(
        @Valid @RequestBody CreateBabyRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        CreateBabyResponse response = babyService.createBaby(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{babyId}")
    public ResponseEntity<ApiResponse<CreateBabyResponse>> getBaby(@PathVariable Long babyId) {
        CreateBabyResponse response = babyService.getBaby(babyId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
