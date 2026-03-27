package kr.co.growmeal.meal.application;

import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.meal.domain.MealFood;
import kr.co.growmeal.meal.domain.MealFoodRepository;
import kr.co.growmeal.meal.domain.MealLog;
import kr.co.growmeal.meal.domain.MealLogRepository;
import kr.co.growmeal.meal.domain.exception.MealLogNotFoundException;
import kr.co.growmeal.meal.ui.dto.request.CreateMealRequest;
import kr.co.growmeal.meal.ui.dto.request.UpdateMealRequest;
import kr.co.growmeal.meal.ui.dto.response.MealLogResponse;
import kr.co.growmeal.meal.ui.dto.response.MealLogResponse.FoodResponse;
import kr.co.growmeal.meal.ui.dto.response.MealLogsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealLogService {

    private final MealLogRepository mealLogRepository;
    private final MealFoodRepository mealFoodRepository;
    private final UserRepository userRepository;

    @Transactional
    public MealLogResponse createMeal(String email, Long babyId, CreateMealRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        MealLog mealLog = MealLog.builder()
            .babyId(babyId)
            .type(request.type())
            .snackIndex(request.snackIndex())
            .notes(request.notes())
            .reaction(request.reaction())
            .createdByUserId(user.getId())
            .build();
        mealLogRepository.save(mealLog);

        List<MealFood> foods = saveFoods(mealLog.getId(), request.foods());

        return toResponse(mealLog, foods, user.getRole());
    }

    @Transactional(readOnly = true)
    public MealLogsResponse getMeals(String email, Long babyId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<MealLog> mealLogs = mealLogRepository.findByBabyIdAndCreatedAtBetween(babyId, start, end);

        if (mealLogs.isEmpty()) {
            return new MealLogsResponse(date.toString(), List.of());
        }

        List<Long> mealLogIds = mealLogs.stream().map(MealLog::getId).toList();
        List<MealFood> allFoods = mealFoodRepository.findByMealLogIdIn(mealLogIds);
        Map<Long, List<MealFood>> foodsByMealLogId = allFoods.stream()
            .collect(Collectors.groupingBy(MealFood::getMealLogId));

        List<Long> creatorIds = mealLogs.stream().map(MealLog::getCreatedByUserId).distinct().toList();
        Map<Long, String> userRoleMap = userRepository.findAllById(creatorIds).stream()
            .collect(Collectors.toMap(User::getId, User::getRole));

        List<MealLogResponse> meals = mealLogs.stream()
            .map(ml -> toResponse(ml, foodsByMealLogId.getOrDefault(ml.getId(), List.of()),
                userRoleMap.getOrDefault(ml.getCreatedByUserId(), "UNKNOWN")))
            .toList();

        return new MealLogsResponse(date.toString(), meals);
    }

    @Transactional
    public MealLogResponse updateMeal(String email, Long babyId, Long mealId, UpdateMealRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        MealLog mealLog = mealLogRepository.findById(mealId)
            .orElseThrow(MealLogNotFoundException::new);

        mealLog.update(request.type(), request.snackIndex(), request.notes(), request.reaction());

        List<MealFood> foods;
        if (request.foods() != null) {
            mealFoodRepository.deleteByMealLogId(mealId);
            foods = request.foods().stream()
                .map(f -> MealFood.builder()
                    .mealLogId(mealId)
                    .inventoryItemId(f.inventoryItemId())
                    .name(f.name())
                    .build())
                .toList();
            mealFoodRepository.saveAll(foods);
        } else {
            foods = mealFoodRepository.findByMealLogId(mealId);
        }

        return toResponse(mealLog, foods, user.getRole());
    }

    private List<MealFood> saveFoods(Long mealLogId, List<CreateMealRequest.FoodRequest> foodRequests) {
        if (foodRequests == null || foodRequests.isEmpty()) {
            return List.of();
        }
        List<MealFood> foods = foodRequests.stream()
            .map(f -> MealFood.builder()
                .mealLogId(mealLogId)
                .inventoryItemId(f.inventoryItemId())
                .name(f.name())
                .build())
            .toList();
        return mealFoodRepository.saveAll(foods);
    }

    private MealLogResponse toResponse(MealLog mealLog, List<MealFood> foods, String createdByRole) {
        List<FoodResponse> foodResponses = foods.stream()
            .map(f -> new FoodResponse(f.getName(), f.getInventoryItemId()))
            .toList();

        return new MealLogResponse(
            mealLog.getId(),
            mealLog.getType(),
            mealLog.getSnackIndex(),
            foodResponses,
            mealLog.getNotes(),
            mealLog.getReaction(),
            createdByRole,
            mealLog.getCreatedAt()
        );
    }
}
