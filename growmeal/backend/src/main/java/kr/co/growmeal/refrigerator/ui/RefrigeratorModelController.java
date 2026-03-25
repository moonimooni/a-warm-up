package kr.co.growmeal.refrigerator.ui;

import kr.co.growmeal.common.ApiResponse;
import kr.co.growmeal.refrigerator.application.RefrigeratorModelService;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorModelsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refrigerator-models")
@RequiredArgsConstructor
public class RefrigeratorModelController {

    private final RefrigeratorModelService refrigeratorModelService;

    @GetMapping
    public ResponseEntity<ApiResponse<RefrigeratorModelsResponse>> getAllModels() {
        RefrigeratorModelsResponse response = refrigeratorModelService.getAllModels();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
