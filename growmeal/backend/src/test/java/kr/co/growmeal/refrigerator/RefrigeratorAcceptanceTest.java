package kr.co.growmeal.refrigerator;

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
class RefrigeratorAcceptanceTest {

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
    }

    // Given: 로그인 후
    // When: POST /refrigerators 요청
    // Then: 201 Created, refrigeratorId/nickname/model/createdAt 반환
    @Test
    @DisplayName("로그인 후 냉장고 생성 성공")
    void 냉장고_생성_성공() {
        // Given: 회원가입 및 로그인
        String email = "refrigerator-create@example.com";
        String phoneNumber = "01099998888";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // When: POST /refrigerators 요청
        ExtractableResponse<Response> createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of(
                        "nickname", "주방 냉장고",
                        "model", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR"
                ))
                .when().post("/refrigerators")
                .then().extract();

        // Then: 201 Created
        assertThat(createResponse.statusCode()).isEqualTo(201);
        assertThat(createResponse.jsonPath().getLong("refrigeratorId")).isNotNull();
        assertThat(createResponse.jsonPath().getString("nickname")).isEqualTo("주방 냉장고");
        assertThat(createResponse.jsonPath().getString("model")).isEqualTo("SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");
        assertThat(createResponse.jsonPath().getString("createdAt")).isNotNull();
    }

    // Given: 존재하지 않는 냉장고 모델
    // When: POST /refrigerators 요청
    // Then: 400 Bad Request
    @Test
    @DisplayName("존재하지 않는 모델로 냉장고 생성 시 400 에러")
    void 존재하지_않는_모델로_냉장고_생성_실패() {
        // Given: 회원가입 및 로그인
        String email = "refrigerator-invalid@example.com";
        String phoneNumber = "01088887777";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // When: 존재하지 않는 model로 POST /refrigerators 요청
        ExtractableResponse<Response> createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of(
                        "nickname", "주방 냉장고",
                        "model", "INVALID_MODEL"
                ))
                .when().post("/refrigerators")
                .then().extract();

        // Then: 400 Bad Request
        assertThat(createResponse.statusCode()).isEqualTo(400);
    }

    // Given: 인증 없음
    // When: POST /refrigerators 요청
    // Then: 401 Unauthorized
    @Test
    @DisplayName("인증 없이 냉장고 생성 시 401 에러")
    void 인증_없이_냉장고_생성_실패() {
        // Given: 인증 없음

        // When: POST /refrigerators 요청
        ExtractableResponse<Response> createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nickname", "주방 냉장고",
                        "model", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR"
                ))
                .when().post("/refrigerators")
                .then().extract();

        // Then: 401 Unauthorized
        assertThat(createResponse.statusCode()).isEqualTo(401);
    }

    // Given: 로그인 후 냉장고 2개 생성
    // When: GET /refrigerators 요청
    // Then: 200 OK, refrigerators 배열 반환
    @Test
    @DisplayName("로그인 후 내 냉장고 목록 조회 성공")
    void 냉장고_목록_조회_성공() {
        // Given: 회원가입, 로그인, 냉장고 2개 생성
        String email = "refrigerator-list@example.com";
        String phoneNumber = "01077776666";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // 냉장고 2개 생성
        냉장고_생성(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");
        냉장고_생성(accessToken, "김치 냉장고", "LG_DIOS_OBJECT_FOUR_DOOR");

        // When: GET /refrigerators 요청
        ExtractableResponse<Response> listResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/refrigerators")
                .then().extract();

        // Then: 200 OK, refrigerators 배열
        assertThat(listResponse.statusCode()).isEqualTo(200);
        List<Map<String, Object>> refrigerators = listResponse.jsonPath().getList("refrigerators");
        assertThat(refrigerators).hasSize(2);
        assertThat(refrigerators).allSatisfy(r -> {
            assertThat(r.get("refrigeratorId")).isNotNull();
            assertThat(r.get("nickname")).isNotNull();
            assertThat(r.get("model")).isNotNull();
            assertThat(r.get("itemCount")).isEqualTo(0);
            assertThat(r.get("createdAt")).isNotNull();
        });
    }

    // Given: 로그인 후 냉장고 없음
    // When: GET /refrigerators 요청
    // Then: 200 OK, 빈 배열
    @Test
    @DisplayName("냉장고가 없을 때 빈 목록 반환")
    void 냉장고_없을때_빈_목록_조회() {
        // Given: 회원가입, 로그인 (냉장고 없음)
        String email = "refrigerator-empty@example.com";
        String phoneNumber = "01066665555";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // When: GET /refrigerators 요청
        ExtractableResponse<Response> listResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/refrigerators")
                .then().extract();

        // Then: 200 OK, 빈 배열
        assertThat(listResponse.statusCode()).isEqualTo(200);
        List<Map<String, Object>> refrigerators = listResponse.jsonPath().getList("refrigerators");
        assertThat(refrigerators).isEmpty();
    }

    // === Helper Methods ===

    private void 회원가입(String email, String phoneNumber, String password) {
        // 인증번호 요청
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("phoneNumber", phoneNumber))
                .when().post("/auth/phone-verifications");

        // Redis에서 인증코드 조회
        String verificationCode = redisTemplate.opsForValue().get("phone:verification:" + phoneNumber);

        // 인증번호 확인
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("phoneNumber", phoneNumber, "code", verificationCode))
                .when().post("/auth/phone-verifications/confirm");

        // 회원가입
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "phoneNumber", phoneNumber,
                        "password", password,
                        "name", "테스트",
                        "role", "MOM"))
                .when().post("/auth/register");
    }

    private ExtractableResponse<Response> 로그인(String email, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", password))
                .when().post("/auth/login")
                .then().extract();
    }

    private void 냉장고_생성(String accessToken, String nickname, String model) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("nickname", nickname, "model", model))
                .when().post("/refrigerators");
    }
}
