package kr.co.growmeal.meal;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig.class)
class MealLogAcceptanceTest {

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
    @DisplayName("끼니 기록 생성 성공")
    void 끼니_기록_생성_성공() {
        // given: 회원가입 → 로그인 → 아기 등록
        String email = "meal-create@example.com";
        String phoneNumber = "01040404040";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");
        Long babyId = 아기_등록_후_ID_반환(accessToken);

        // when: 끼니 기록 생성
        ExtractableResponse<Response> createResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "type", "BREAKFAST",
                "foods", List.of(Map.of("name", "쌀밥")),
                "notes", "잘 먹었어요",
                "reaction", "GOOD"
            ))
            .when().post("/babies/" + babyId + "/meals")
            .then().extract();

        // then
        assertThat(createResponse.statusCode()).isEqualTo(201);
        assertThat(createResponse.jsonPath().getLong("data.mealId")).isNotNull();
        assertThat(createResponse.jsonPath().getString("data.type")).isEqualTo("BREAKFAST");
        assertThat(createResponse.jsonPath().getList("data.foods")).hasSize(1);
        assertThat(createResponse.jsonPath().getString("data.foods[0].name")).isEqualTo("쌀밥");
        assertThat(createResponse.jsonPath().getString("data.notes")).isEqualTo("잘 먹었어요");
        assertThat(createResponse.jsonPath().getString("data.reaction")).isEqualTo("GOOD");
        assertThat(createResponse.jsonPath().getString("data.createdBy")).isEqualTo("MOM");
        assertThat(createResponse.jsonPath().getString("data.createdAt")).isNotNull();
    }

    @Test
    @DisplayName("날짜별 끼니 기록 조회 성공")
    void 날짜별_끼니_기록_조회_성공() {
        // given: 회원가입 → 로그인 → 아기 등록 → 끼니 생성
        String email = "meal-list@example.com";
        String phoneNumber = "01041414141";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");
        Long babyId = 아기_등록_후_ID_반환(accessToken);

        끼니_생성_후_ID_반환(accessToken, babyId, "BREAKFAST",
            List.of(Map.of("name", "쌀밥")), "잘 먹었어요", "GOOD");

        // when: 오늘 날짜로 끼니 기록 조회
        String today = LocalDate.now().toString();
        ExtractableResponse<Response> listResponse = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .queryParam("date", today)
            .when().get("/babies/" + babyId + "/meals")
            .then().extract();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(200);
        assertThat(listResponse.jsonPath().getString("data.date")).isEqualTo(today);
        assertThat(listResponse.jsonPath().getList("data.meals")).isNotEmpty();
        assertThat(listResponse.jsonPath().getString("data.meals[0].type")).isEqualTo("BREAKFAST");
    }

    @Test
    @DisplayName("끼니 기록 수정 성공")
    void 끼니_기록_수정_성공() {
        // given: 회원가입 → 로그인 → 아기 등록 → 끼니 생성
        String email = "meal-update@example.com";
        String phoneNumber = "01042424242";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");
        Long babyId = 아기_등록_후_ID_반환(accessToken);

        Long mealId = 끼니_생성_후_ID_반환(accessToken, babyId, "BREAKFAST",
            List.of(Map.of("name", "쌀밥")), "잘 먹었어요", "GOOD");

        // when: 끼니 기록 수정
        ExtractableResponse<Response> updateResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "notes", "조금 먹었어요",
                "reaction", "NEUTRAL"
            ))
            .when().put("/babies/" + babyId + "/meals/" + mealId)
            .then().extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(200);
        assertThat(updateResponse.jsonPath().getLong("data.mealId")).isEqualTo(mealId);
        assertThat(updateResponse.jsonPath().getString("data.notes")).isEqualTo("조금 먹었어요");
        assertThat(updateResponse.jsonPath().getString("data.reaction")).isEqualTo("NEUTRAL");
        assertThat(updateResponse.jsonPath().getString("data.type")).isEqualTo("BREAKFAST");
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

    private Long 아기_등록_후_ID_반환(String accessToken) {
        ExtractableResponse<Response> response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", "하율",
                "birthDate", "2024-01-15",
                "allergies", List.of("땅콩")
            ))
            .when().post("/babies")
            .then().extract();
        return response.jsonPath().getLong("data.babyId");
    }

    private Long 끼니_생성_후_ID_반환(String accessToken, Long babyId, String type,
                                  List<Map<String, String>> foods, String notes, String reaction) {
        ExtractableResponse<Response> response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "type", type,
                "foods", foods,
                "notes", notes,
                "reaction", reaction
            ))
            .when().post("/babies/" + babyId + "/meals")
            .then().extract();
        return response.jsonPath().getLong("data.mealId");
    }
}
