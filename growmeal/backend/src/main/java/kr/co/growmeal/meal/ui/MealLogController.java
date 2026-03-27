package kr.co.growmeal.meal.ui;

import kr.co.growmeal.common.ApiResponse;
import kr.co.growmeal.meal.application.MealLogService;
import kr.co.growmeal.meal.ui.dto.request.CreateMealRequest;
import kr.co.growmeal.meal.ui.dto.request.UpdateMealRequest;
import kr.co.growmeal.meal.ui.dto.response.MealLogResponse;
import kr.co.growmeal.meal.ui.dto.response.MealLogsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/babies/{babyId}/meals")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    @PostMapping
    public ResponseEntity<ApiResponse<MealLogResponse>> createMeal(
        @PathVariable Long babyId,
        @RequestBody CreateMealRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        MealLogResponse response = mealLogService.createMeal(email, babyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MealLogsResponse>> getMeals(
        @PathVariable Long babyId,
        @RequestParam String date,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        LocalDate localDate = LocalDate.parse(date);
        MealLogsResponse response = mealLogService.getMeals(email, babyId, localDate);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{mealId}")
    public ResponseEntity<ApiResponse<MealLogResponse>> updateMeal(
        @PathVariable Long babyId,
        @PathVariable Long mealId,
        @RequestBody UpdateMealRequest request,
        Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        MealLogResponse response = mealLogService.updateMeal(email, babyId, mealId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
