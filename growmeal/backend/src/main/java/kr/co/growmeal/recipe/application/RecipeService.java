package kr.co.growmeal.recipe.application;

import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.ingredient.domain.IngredientMaster;
import kr.co.growmeal.ingredient.domain.IngredientMasterAllergyRepository;
import kr.co.growmeal.ingredient.domain.IngredientMasterRepository;
import kr.co.growmeal.inventory.domain.InventoryItem;
import kr.co.growmeal.inventory.domain.InventoryItemRepository;
import kr.co.growmeal.recipe.domain.*;
import kr.co.growmeal.recipe.domain.exception.RecipeNotFoundException;
import kr.co.growmeal.recipe.ui.dto.request.CreateRecipeRequest;
import kr.co.growmeal.recipe.ui.dto.request.UpdateRecipeRequest;
import kr.co.growmeal.recipe.ui.dto.response.DeleteRecipeResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipeDetailResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipeDetailResponse.MissingIngredientResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipeResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipeResponse.IngredientResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipeResponse.StepResponse;
import kr.co.growmeal.recipe.ui.dto.response.RecipesResponse;
import kr.co.growmeal.refrigerator.domain.Refrigerator;
import kr.co.growmeal.refrigerator.domain.RefrigeratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientMasterRepository ingredientMasterRepository;
    private final IngredientMasterAllergyRepository ingredientMasterAllergyRepository;
    private final UserRepository userRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Transactional
    public RecipeResponse createRecipe(CreateRecipeRequest request) {
        Recipe recipe = recipeRepository.save(
            Recipe.builder()
                .name(request.name())
                .difficulty(request.difficulty())
                .build()
        );

        List<RecipeStep> steps = recipeStepRepository.saveAll(
            request.steps().stream()
                .map(s -> RecipeStep.builder()
                    .recipeId(recipe.getId())
                    .step(s.step())
                    .description(s.description())
                    .image(s.image())
                    .build())
                .toList()
        );

        List<RecipeIngredient> ingredients = recipeIngredientRepository.saveAll(
            request.ingredients().stream()
                .map(i -> {
                    var master = ingredientMasterRepository.findByName(i.name());
                    return RecipeIngredient.builder()
                        .recipeId(recipe.getId())
                        .ingredientMasterId(master.map(IngredientMaster::getId).orElse(null))
                        .name(master.isEmpty() ? i.name() : null)
                        .amount(i.amount())
                        .build();
                })
                .toList()
        );

        NutrientAllergyResult result = calculateNutrientsAndAllergies(ingredients);

        List<StepResponse> stepResponses = steps.stream()
            .map(s -> new StepResponse(s.getStep(), s.getDescription(), s.getImage()))
            .toList();

        List<IngredientResponse> ingredientResponses = request.ingredients().stream()
            .map(i -> new IngredientResponse(i.name(), i.amount()))
            .toList();

        return new RecipeResponse(
            recipe.getId(),
            recipe.getName(),
            recipe.getDifficulty(),
            stepResponses,
            result.nutrients(),
            ingredientResponses,
            result.allergyWarnings(),
            recipe.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public RecipesResponse getRecipes(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        Set<String> inventoryItemNames = getInventoryItemNames(user.getId());

        List<Recipe> recipes = recipeRepository.findAll();
        if (recipes.isEmpty()) {
            return new RecipesResponse(List.of());
        }

        List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
        Map<Long, List<RecipeStep>> stepsByRecipeId = recipeStepRepository.findByRecipeIdIn(recipeIds).stream()
            .collect(Collectors.groupingBy(RecipeStep::getRecipeId));
        Map<Long, List<RecipeIngredient>> ingredientsByRecipeId = recipeIngredientRepository.findByRecipeIdIn(recipeIds).stream()
            .collect(Collectors.groupingBy(RecipeIngredient::getRecipeId));

        List<Long> allMasterIds = ingredientsByRecipeId.values().stream()
            .flatMap(List::stream)
            .map(RecipeIngredient::getIngredientMasterId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<Long, IngredientMaster> masterMap = allMasterIds.isEmpty()
            ? Map.of()
            : ingredientMasterRepository.findAllById(allMasterIds).stream()
                .collect(Collectors.toMap(IngredientMaster::getId, m -> m));

        List<RecipeDetailResponse> details = recipes.stream()
            .map(recipe -> {
                List<RecipeStep> steps = stepsByRecipeId.getOrDefault(recipe.getId(), List.of());
                List<RecipeIngredient> ingredients = ingredientsByRecipeId.getOrDefault(recipe.getId(), List.of());

                NutrientAllergyResult result = calculateNutrientsAndAllergies(ingredients);

                List<StepResponse> stepResponses = steps.stream()
                    .sorted(Comparator.comparingInt(RecipeStep::getStep))
                    .map(s -> new StepResponse(s.getStep(), s.getDescription(), s.getImage()))
                    .toList();

                List<IngredientResponse> ingredientResponses = ingredients.stream()
                    .map(i -> new IngredientResponse(
                        resolveIngredientName(i, masterMap),
                        i.getAmount()
                    ))
                    .toList();

                List<MissingIngredientResponse> missingIngredients = ingredients.stream()
                    .filter(i -> {
                        String name = resolveIngredientName(i, masterMap);
                        return !inventoryItemNames.contains(name);
                    })
                    .map(i -> new MissingIngredientResponse(
                        resolveIngredientName(i, masterMap),
                        i.getAmount()
                    ))
                    .toList();

                return new RecipeDetailResponse(
                    recipe.getId(),
                    recipe.getName(),
                    recipe.getDifficulty(),
                    stepResponses,
                    result.nutrients(),
                    ingredientResponses,
                    missingIngredients,
                    result.allergyWarnings(),
                    recipe.getCreatedAt()
                );
            })
            .toList();

        return new RecipesResponse(details);
    }

    @Transactional(readOnly = true)
    public RecipeDetailResponse getRecipe(Long recipeId, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(RecipeNotFoundException::new);

        Set<String> inventoryItemNames = getInventoryItemNames(user.getId());
        return buildRecipeDetailResponse(recipe, inventoryItemNames);
    }

    @Transactional
    public RecipeDetailResponse updateRecipe(Long recipeId, UpdateRecipeRequest request, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(RecipeNotFoundException::new);

        recipe.update(request.name(), request.difficulty());

        if (request.steps() != null) {
            recipeStepRepository.deleteByRecipeId(recipeId);
            recipeStepRepository.saveAll(
                request.steps().stream()
                    .map(s -> RecipeStep.builder()
                        .recipeId(recipeId)
                        .step(s.step())
                        .description(s.description())
                        .image(s.image())
                        .build())
                    .toList()
            );
        }

        if (request.ingredients() != null) {
            recipeIngredientRepository.deleteByRecipeId(recipeId);
            recipeIngredientRepository.saveAll(
                request.ingredients().stream()
                    .map(i -> {
                        var master = ingredientMasterRepository.findByName(i.name());
                        return RecipeIngredient.builder()
                            .recipeId(recipeId)
                            .ingredientMasterId(master.map(IngredientMaster::getId).orElse(null))
                            .name(master.isEmpty() ? i.name() : null)
                            .amount(i.amount())
                            .build();
                    })
                    .toList()
            );
        }

        Set<String> inventoryItemNames = getInventoryItemNames(user.getId());
        return buildRecipeDetailResponse(recipe, inventoryItemNames);
    }

    @Transactional
    public DeleteRecipeResponse deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(RecipeNotFoundException::new);

        recipeStepRepository.deleteByRecipeId(recipeId);
        recipeIngredientRepository.deleteByRecipeId(recipeId);
        recipeRepository.delete(recipe);

        return new DeleteRecipeResponse("레시피가 삭제되었습니다.", recipeId);
    }

    private RecipeDetailResponse buildRecipeDetailResponse(Recipe recipe, Set<String> inventoryItemNames) {
        List<RecipeStep> steps = recipeStepRepository.findByRecipeIdOrderByStep(recipe.getId());
        List<RecipeIngredient> ingredients = recipeIngredientRepository.findByRecipeId(recipe.getId());

        NutrientAllergyResult result = calculateNutrientsAndAllergies(ingredients);

        List<Long> masterIds = ingredients.stream()
            .map(RecipeIngredient::getIngredientMasterId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<Long, IngredientMaster> masterMap = masterIds.isEmpty()
            ? Map.of()
            : ingredientMasterRepository.findAllById(masterIds).stream()
                .collect(Collectors.toMap(IngredientMaster::getId, m -> m));

        List<StepResponse> stepResponses = steps.stream()
            .map(s -> new StepResponse(s.getStep(), s.getDescription(), s.getImage()))
            .toList();

        List<IngredientResponse> ingredientResponses = ingredients.stream()
            .map(i -> new IngredientResponse(resolveIngredientName(i, masterMap), i.getAmount()))
            .toList();

        List<MissingIngredientResponse> missingIngredients = ingredients.stream()
            .filter(i -> !inventoryItemNames.contains(resolveIngredientName(i, masterMap)))
            .map(i -> new MissingIngredientResponse(resolveIngredientName(i, masterMap), i.getAmount()))
            .toList();

        return new RecipeDetailResponse(
            recipe.getId(),
            recipe.getName(),
            recipe.getDifficulty(),
            stepResponses,
            result.nutrients(),
            ingredientResponses,
            missingIngredients,
            result.allergyWarnings(),
            recipe.getCreatedAt()
        );
    }

    private Set<String> getInventoryItemNames(Long userId) {
        List<Long> refrigeratorIds = refrigeratorRepository.findByUserId(userId).stream()
            .map(Refrigerator::getId)
            .toList();

        if (refrigeratorIds.isEmpty()) {
            return Set.of();
        }

        return inventoryItemRepository.findByRefrigeratorIdIn(refrigeratorIds).stream()
            .map(InventoryItem::getName)
            .collect(Collectors.toSet());
    }

    private String resolveIngredientName(RecipeIngredient ingredient, Map<Long, IngredientMaster> masterMap) {
        if (ingredient.getIngredientMasterId() != null) {
            IngredientMaster master = masterMap.get(ingredient.getIngredientMasterId());
            return master != null ? master.getName() : ingredient.getName();
        }
        return ingredient.getName();
    }

    private NutrientAllergyResult calculateNutrientsAndAllergies(List<RecipeIngredient> ingredients) {
        List<Long> masterIds = ingredients.stream()
            .map(RecipeIngredient::getIngredientMasterId)
            .filter(Objects::nonNull)
            .toList();

        if (masterIds.isEmpty()) {
            return new NutrientAllergyResult(List.of(), List.of());
        }

        List<IngredientMaster> masters = ingredientMasterRepository.findAllById(masterIds);
        List<String> nutrients = masters.stream()
            .flatMap(m -> Stream.of(m.getMainNutrient(), m.getExtraNutrient()))
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        List<String> allergies = ingredientMasterAllergyRepository
            .findByIngredientMasterIdIn(masterIds).stream()
            .map(a -> a.getAllergyInfo())
            .distinct()
            .toList();

        return new NutrientAllergyResult(nutrients, allergies);
    }

    private record NutrientAllergyResult(List<String> nutrients, List<String> allergyWarnings) {
    }
}
