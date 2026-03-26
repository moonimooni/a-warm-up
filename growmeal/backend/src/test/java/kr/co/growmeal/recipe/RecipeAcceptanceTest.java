package kr.co.growmeal.recipe;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kr.co.growmeal.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig.class)
class RecipeAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", EmbeddedRedisConfig::getRedisPort);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    @DisplayName("레시피 추가 성공")
    void 레시피_추가_성공() {
        // given
        String email = "recipe-create@example.com";
        String phoneNumber = "01030303030";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");

        // when
        ExtractableResponse<Response> createResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", "두부미역국",
                "difficulty", "MEDIUM",
                "steps", List.of(
                    Map.of("step", 1, "description", "두부를 깍둑썬다"),
                    Map.of("step", 2, "description", "미역을 불린다"),
                    Map.of("step", 3, "description", "함께 끓인다")
                ),
                "ingredients", List.of(
                    Map.of("name", "두부", "amount", "50g"),
                    Map.of("name", "미역", "amount", "30g"),
                    Map.of("name", "마늘", "amount", "5g")
                )
            ))
            .when().post("/recipes")
            .then().extract();

        // then
        assertThat(createResponse.statusCode()).isEqualTo(201);
        assertThat(createResponse.jsonPath().getLong("data.recipeId")).isNotNull();
        assertThat(createResponse.jsonPath().getString("data.name")).isEqualTo("두부미역국");
        assertThat(createResponse.jsonPath().getString("data.difficulty")).isEqualTo("MEDIUM");
        assertThat(createResponse.jsonPath().getList("data.steps")).hasSize(3);
        assertThat(createResponse.jsonPath().getList("data.ingredients")).hasSize(3);
        assertThat(createResponse.jsonPath().getList("data.nutrients"))
            .containsAnyOf("PROTEIN", "CALCIUM", "IRON");
        assertThat(createResponse.jsonPath().getList("data.allergyWarnings"))
            .contains("대두");
        assertThat(createResponse.jsonPath().getString("data.createdAt")).isNotNull();
    }

    @Test
    @DisplayName("보유 재료 기반 레시피 목록 조회 성공")
    void 레시피_목록_조회_성공() {
        // given
        String email = "recipe-list@example.com";
        String phoneNumber = "01031313131";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");

        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");
        인벤토리_생성(accessToken, refrigeratorId, "두부", "INGREDIENT", "bkf_4", "2026-03-30");

        레시피_생성(accessToken, "두부미역국", "MEDIUM",
            List.of(Map.of("step", 1, "description", "끓인다")),
            List.of(
                Map.of("name", "두부", "amount", "50g"),
                Map.of("name", "미역", "amount", "30g")
            )
        );

        // when
        ExtractableResponse<Response> listResponse = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .when().get("/recipes")
            .then().extract();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(200);
        List<Map<String, Object>> recipes = listResponse.jsonPath().getList("data.recipes");
        assertThat(recipes).isNotEmpty();

        Map<String, Object> recipe = recipes.get(0);
        assertThat(recipe.get("name")).isEqualTo("두부미역국");
        assertThat((List<?>) recipe.get("nutrients")).isNotEmpty();
        assertThat(listResponse.jsonPath().getList("data.recipes[0].allergyWarnings", String.class)).contains("대두");

        List<Map<String, Object>> missingIngredients = listResponse.jsonPath().getList("data.recipes[0].missingIngredients");
        List<String> missingNames = missingIngredients.stream()
            .map(m -> (String) m.get("name"))
            .toList();
        assertThat(missingNames).contains("미역");
        assertThat(missingNames).doesNotContain("두부");
    }

    private void 회원가입(String email, String phoneNumber, String password) {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of("phoneNumber", phoneNumber))
            .when().post("/auth/phone-verifications");

        String verificationCode = redisTemplate.opsForValue().get("phone:verification:" + phoneNumber);

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of("phoneNumber", phoneNumber, "code", verificationCode))
            .when().post("/auth/phone-verifications/confirm");

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "email", email,
                "phoneNumber", phoneNumber,
                "password", password,
                "name", "테스트",
                "role", "MOM"
            ))
            .when().post("/auth/register");
    }

    private ExtractableResponse<Response> 로그인(String email, String password) {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of("email", email, "password", password))
            .when().post("/auth/login")
            .then().extract();
    }

    private Long 냉장고_생성_후_ID_반환(String accessToken, String nickname, String model) {
        ExtractableResponse<Response> response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of("nickname", nickname, "model", model))
            .when().post("/refrigerators")
            .then().extract();
        return response.jsonPath().getLong("data.refrigeratorId");
    }

    private void 인벤토리_생성(String accessToken, Long refrigeratorId, String name, String type, String compartmentId, String expiresAt) {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", name,
                "type", type,
                "refrigeratorId", refrigeratorId,
                "compartmentId", compartmentId,
                "expiresAt", expiresAt
            ))
            .when().post("/inventory");
    }

    private void 레시피_생성(String accessToken, String name, String difficulty, List<Map<String, Object>> steps, List<Map<String, String>> ingredients) {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", name,
                "difficulty", difficulty,
                "steps", steps,
                "ingredients", ingredients
            ))
            .when().post("/recipes");
    }
}
