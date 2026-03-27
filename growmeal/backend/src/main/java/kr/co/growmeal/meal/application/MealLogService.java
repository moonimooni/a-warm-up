package kr.co.growmeal.meal.application;

import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.ingredient.domain.IngredientMaster;
import kr.co.growmeal.ingredient.domain.IngredientMasterAllergyRepository;
import kr.co.growmeal.ingredient.domain.IngredientMasterRepository;
import kr.co.growmeal.inventory.domain.InventoryItem;
import kr.co.growmeal.inventory.domain.InventoryItemRepository;
import kr.co.growmeal.meal.domain.MealFood;
import kr.co.growmeal.meal.domain.MealFoodRepository;
import kr.co.growmeal.meal.domain.MealLog;
import kr.co.growmeal.meal.domain.MealLogRepository;
import kr.co.growmeal.meal.domain.exception.MealLogNotFoundException;
import kr.co.growmeal.meal.ui.dto.request.CreateMealRequest;
import kr.co.growmeal.meal.ui.dto.request.UpdateMealRequest;
import kr.co.growmeal.meal.ui.dto.response.DeleteMealResponse;
import kr.co.growmeal.meal.ui.dto.response.MealLogResponse;
import kr.co.growmeal.meal.ui.dto.response.MealLogResponse.FoodResponse;
import kr.co.growmeal.meal.ui.dto.response.MealLogsResponse;
import kr.co.growmeal.meal.ui.dto.response.MealRecommendationResponse;
import kr.co.growmeal.meal.ui.dto.response.MealRecommendationResponse.InventoryRecommendation;
import kr.co.growmeal.meal.ui.dto.response.MealRecommendationResponse.RecommendationGroup;
import kr.co.growmeal.refrigerator.domain.Refrigerator;
import kr.co.growmeal.refrigerator.domain.RefrigeratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MealLogService {

    private final MealLogRepository mealLogRepository;
    private final MealFoodRepository mealFoodRepository;
    private final UserRepository userRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final IngredientMasterRepository ingredientMasterRepository;
    private final IngredientMasterAllergyRepository ingredientMasterAllergyRepository;

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

    @Transactional(readOnly = true)
    public MealLogResponse getMeal(String email, Long babyId, Long mealId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        MealLog mealLog = mealLogRepository.findById(mealId)
            .orElseThrow(MealLogNotFoundException::new);

        List<MealFood> foods = mealFoodRepository.findByMealLogId(mealId);
        String creatorRole = userRepository.findById(mealLog.getCreatedByUserId())
            .map(User::getRole)
            .orElse("UNKNOWN");

        return toResponse(mealLog, foods, creatorRole);
    }

    @Transactional
    public DeleteMealResponse deleteMeal(String email, Long babyId, Long mealId) {
        MealLog mealLog = mealLogRepository.findById(mealId)
            .orElseThrow(MealLogNotFoundException::new);

        mealFoodRepository.deleteByMealLogId(mealId);
        mealLogRepository.delete(mealLog);

        return new DeleteMealResponse("끼니 기록이 삭제되었습니다.", mealId);
    }

    @Transactional(readOnly = true)
    public MealRecommendationResponse getRecommendations(String email, Long babyId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        // 당일 끼니 기록 조회
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        List<MealLog> todayMeals = mealLogRepository.findByBabyIdAndCreatedAtBetween(babyId, start, end);

        // 당일 첫끼라면 한 주간의 끼니를 기반으로
        List<MealLog> referenceMeals;
        if (todayMeals.isEmpty()) {
            LocalDateTime weekStart = today.minusDays(7).atStartOfDay();
            referenceMeals = mealLogRepository.findByBabyIdAndCreatedAtBetween(babyId, weekStart, end);
        } else {
            referenceMeals = todayMeals;
        }

        // 먹은 음식의 영양소 수집
        Set<String> consumedNutrients = getConsumedNutrients(referenceMeals);

        // 사용자의 냉장고 인벤토리 아이템 조회
        List<Long> refrigeratorIds = refrigeratorRepository.findByUserId(user.getId()).stream()
            .map(Refrigerator::getId)
            .toList();

        if (refrigeratorIds.isEmpty()) {
            return new MealRecommendationResponse(List.of());
        }

        List<InventoryItem> allItems = inventoryItemRepository.findByRefrigeratorIdIn(refrigeratorIds);

        // 각 아이템의 영양소 조회하여 부족한 영양소를 채울 수 있는 아이템 추천
        List<InventoryRecommendation> recommendations = allItems.stream()
            .filter(item -> {
                Optional<IngredientMaster> master = ingredientMasterRepository.findByName(item.getName());
                if (master.isEmpty()) return false;
                IngredientMaster m = master.get();
                return (m.getMainNutrient() != null && !consumedNutrients.contains(m.getMainNutrient()))
                    || (m.getExtraNutrient() != null && !consumedNutrients.contains(m.getExtraNutrient()));
            })
            .map(item -> {
                IngredientMaster master = ingredientMasterRepository.findByName(item.getName()).orElse(null);
                List<String> nutrients = master != null
                    ? Stream.of(master.getMainNutrient(), master.getExtraNutrient())
                        .filter(Objects::nonNull).toList()
                    : List.of();
                List<String> allergyInfo = master != null
                    ? ingredientMasterAllergyRepository.findByIngredientMasterId(master.getId()).stream()
                        .map(a -> a.getAllergyInfo()).toList()
                    : List.of();
                return new InventoryRecommendation(
                    item.getId(), item.getName(), item.getType().name(),
                    item.getRefrigeratorId(), item.getCompartmentId(),
                    nutrients, allergyInfo,
                    item.getCreatedAt(), item.getExpiresAt()
                );
            })
            .toList();

        return new MealRecommendationResponse(List.of(new RecommendationGroup(recommendations)));
    }

    private Set<String> getConsumedNutrients(List<MealLog> meals) {
        if (meals.isEmpty()) return Set.of();

        List<Long> mealLogIds = meals.stream().map(MealLog::getId).toList();
        List<MealFood> allFoods = mealFoodRepository.findByMealLogIdIn(mealLogIds);

        List<String> foodNames = allFoods.stream()
            .map(MealFood::getName)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        Set<String> nutrients = new HashSet<>();
        for (String foodName : foodNames) {
            ingredientMasterRepository.findByName(foodName).ifPresent(master -> {
                if (master.getMainNutrient() != null) nutrients.add(master.getMainNutrient());
                if (master.getExtraNutrient() != null) nutrients.add(master.getExtraNutrient());
            });
        }
        return nutrients;
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
