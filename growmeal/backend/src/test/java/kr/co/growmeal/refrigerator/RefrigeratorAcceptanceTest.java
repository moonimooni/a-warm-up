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

    // === GET /refrigerators/:refrigeratorId ===

    // Given: 로그인 후 본인 냉장고
    // When: GET /refrigerators/:refrigeratorId 요청
    // Then: 200 OK, compartments 포함 상세 정보 반환
    @Test
    @DisplayName("본인 냉장고 상세 조회 성공")
    void 냉장고_상세_조회_성공() {
        // Given: 회원가입, 로그인, 냉장고 생성
        String email = "refrigerator-detail@example.com";
        String phoneNumber = "01055554444";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // When: GET /refrigerators/:refrigeratorId 요청
        ExtractableResponse<Response> detailResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/refrigerators/" + refrigeratorId)
                .then().extract();

        // Then: 200 OK, compartments 포함
        assertThat(detailResponse.statusCode()).isEqualTo(200);
        assertThat(detailResponse.jsonPath().getLong("refrigeratorId")).isEqualTo(refrigeratorId);
        assertThat(detailResponse.jsonPath().getString("nickname")).isEqualTo("주방 냉장고");
        assertThat(detailResponse.jsonPath().getString("model")).isEqualTo("SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");
        assertThat(detailResponse.jsonPath().getList("compartments")).isNotNull();
        assertThat(detailResponse.jsonPath().getString("createdAt")).isNotNull();
    }

    // Given: 존재하지 않는 냉장고 ID
    // When: GET /refrigerators/:refrigeratorId 요청
    // Then: 404 Not Found
    @Test
    @DisplayName("존재하지 않는 냉장고 상세 조회 시 404 에러")
    void 존재하지_않는_냉장고_상세_조회_실패() {
        // Given: 회원가입, 로그인
        String email = "refrigerator-detail-notfound@example.com";
        String phoneNumber = "01044443333";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // When: 존재하지 않는 ID로 GET /refrigerators/:refrigeratorId 요청
        ExtractableResponse<Response> detailResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/refrigerators/999999")
                .then().extract();

        // Then: 404 Not Found
        assertThat(detailResponse.statusCode()).isEqualTo(404);
    }

    // Given: 다른 사용자의 냉장고
    // When: GET /refrigerators/:refrigeratorId 요청
    // Then: 404 Not Found (보안상)
    @Test
    @DisplayName("다른 사용자 냉장고 상세 조회 시 404 에러")
    void 다른_사용자_냉장고_상세_조회_실패() {
        // Given: 사용자1 회원가입, 로그인, 냉장고 생성
        String email1 = "refrigerator-owner@example.com";
        String phoneNumber1 = "01033332222";
        String password = "Test123!@#";

        회원가입(email1, phoneNumber1, password);
        ExtractableResponse<Response> loginResponse1 = 로그인(email1, password);
        String accessToken1 = loginResponse1.jsonPath().getString("accessToken");
        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken1, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // 사용자2 회원가입, 로그인
        String email2 = "refrigerator-other@example.com";
        String phoneNumber2 = "01022221111";

        회원가입(email2, phoneNumber2, password);
        ExtractableResponse<Response> loginResponse2 = 로그인(email2, password);
        String accessToken2 = loginResponse2.jsonPath().getString("accessToken");

        // When: 사용자2가 사용자1의 냉장고 조회 시도
        ExtractableResponse<Response> detailResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken2)
                .when().get("/refrigerators/" + refrigeratorId)
                .then().extract();

        // Then: 404 Not Found
        assertThat(detailResponse.statusCode()).isEqualTo(404);
    }

    // === PUT /refrigerators/:refrigeratorId ===

    // Given: 로그인 후 본인 냉장고
    // When: PUT /refrigerators/:refrigeratorId 요청
    // Then: 200 OK, 수정된 정보 반환
    @Test
    @DisplayName("본인 냉장고 수정 성공")
    void 냉장고_수정_성공() {
        // Given: 회원가입, 로그인, 냉장고 생성
        String email = "refrigerator-update@example.com";
        String phoneNumber = "01011110000";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // When: PUT /refrigerators/:refrigeratorId 요청
        ExtractableResponse<Response> updateResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("nickname", "우리집 냉장고"))
                .when().put("/refrigerators/" + refrigeratorId)
                .then().extract();

        // Then: 200 OK
        assertThat(updateResponse.statusCode()).isEqualTo(200);
        assertThat(updateResponse.jsonPath().getLong("refrigeratorId")).isEqualTo(refrigeratorId);
        assertThat(updateResponse.jsonPath().getString("nickname")).isEqualTo("우리집 냉장고");
        assertThat(updateResponse.jsonPath().getString("model")).isEqualTo("SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");
        assertThat(updateResponse.jsonPath().getString("updatedAt")).isNotNull();
    }

    // Given: 존재하지 않는 냉장고 ID
    // When: PUT /refrigerators/:refrigeratorId 요청
    // Then: 404 Not Found
    @Test
    @DisplayName("존재하지 않는 냉장고 수정 시 404 에러")
    void 존재하지_않는_냉장고_수정_실패() {
        // Given: 회원가입, 로그인
        String email = "refrigerator-update-notfound@example.com";
        String phoneNumber = "01000009999";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // When: 존재하지 않는 ID로 PUT /refrigerators/:refrigeratorId 요청
        ExtractableResponse<Response> updateResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("nickname", "우리집 냉장고"))
                .when().put("/refrigerators/999999")
                .then().extract();

        // Then: 404 Not Found
        assertThat(updateResponse.statusCode()).isEqualTo(404);
    }

    // Given: 다른 사용자의 냉장고
    // When: PUT /refrigerators/:refrigeratorId 요청
    // Then: 404 Not Found
    @Test
    @DisplayName("다른 사용자 냉장고 수정 시 404 에러")
    void 다른_사용자_냉장고_수정_실패() {
        // Given: 사용자1 회원가입, 로그인, 냉장고 생성
        String email1 = "refrigerator-update-owner@example.com";
        String phoneNumber1 = "01099990000";
        String password = "Test123!@#";

        회원가입(email1, phoneNumber1, password);
        ExtractableResponse<Response> loginResponse1 = 로그인(email1, password);
        String accessToken1 = loginResponse1.jsonPath().getString("accessToken");
        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken1, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // 사용자2 회원가입, 로그인
        String email2 = "refrigerator-update-other@example.com";
        String phoneNumber2 = "01088880000";

        회원가입(email2, phoneNumber2, password);
        ExtractableResponse<Response> loginResponse2 = 로그인(email2, password);
        String accessToken2 = loginResponse2.jsonPath().getString("accessToken");

        // When: 사용자2가 사용자1의 냉장고 수정 시도
        ExtractableResponse<Response> updateResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken2)
                .body(Map.of("nickname", "우리집 냉장고"))
                .when().put("/refrigerators/" + refrigeratorId)
                .then().extract();

        // Then: 404 Not Found
        assertThat(updateResponse.statusCode()).isEqualTo(404);
    }

    // === DELETE /refrigerators/:refrigeratorId ===

    // Given: 로그인 후 본인 냉장고
    // When: DELETE /refrigerators/:refrigeratorId 요청
    // Then: 204 No Content
    @Test
    @DisplayName("본인 냉장고 삭제 성공")
    void 냉장고_삭제_성공() {
        // Given: 회원가입, 로그인, 냉장고 생성
        String email = "refrigerator-delete@example.com";
        String phoneNumber = "01077770000";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // When: DELETE /refrigerators/:refrigeratorId 요청
        ExtractableResponse<Response> deleteResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/refrigerators/" + refrigeratorId)
                .then().extract();

        // Then: 204 No Content
        assertThat(deleteResponse.statusCode()).isEqualTo(204);

        // 삭제 확인: 조회 시 404
        ExtractableResponse<Response> verifyResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/refrigerators/" + refrigeratorId)
                .then().extract();
        assertThat(verifyResponse.statusCode()).isEqualTo(404);
    }

    // Given: 존재하지 않는 냉장고 ID
    // When: DELETE /refrigerators/:refrigeratorId 요청
    // Then: 404 Not Found
    @Test
    @DisplayName("존재하지 않는 냉장고 삭제 시 404 에러")
    void 존재하지_않는_냉장고_삭제_실패() {
        // Given: 회원가입, 로그인
        String email = "refrigerator-delete-notfound@example.com";
        String phoneNumber = "01066660000";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // When: 존재하지 않는 ID로 DELETE /refrigerators/:refrigeratorId 요청
        ExtractableResponse<Response> deleteResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/refrigerators/999999")
                .then().extract();

        // Then: 404 Not Found
        assertThat(deleteResponse.statusCode()).isEqualTo(404);
    }

    // Given: 다른 사용자의 냉장고
    // When: DELETE /refrigerators/:refrigeratorId 요청
    // Then: 404 Not Found
    @Test
    @DisplayName("다른 사용자 냉장고 삭제 시 404 에러")
    void 다른_사용자_냉장고_삭제_실패() {
        // Given: 사용자1 회원가입, 로그인, 냉장고 생성
        String email1 = "refrigerator-delete-owner@example.com";
        String phoneNumber1 = "01055550000";
        String password = "Test123!@#";

        회원가입(email1, phoneNumber1, password);
        ExtractableResponse<Response> loginResponse1 = 로그인(email1, password);
        String accessToken1 = loginResponse1.jsonPath().getString("accessToken");
        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken1, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // 사용자2 회원가입, 로그인
        String email2 = "refrigerator-delete-other@example.com";
        String phoneNumber2 = "01044440000";

        회원가입(email2, phoneNumber2, password);
        ExtractableResponse<Response> loginResponse2 = 로그인(email2, password);
        String accessToken2 = loginResponse2.jsonPath().getString("accessToken");

        // When: 사용자2가 사용자1의 냉장고 삭제 시도
        ExtractableResponse<Response> deleteResponse = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken2)
                .when().delete("/refrigerators/" + refrigeratorId)
                .then().extract();

        // Then: 404 Not Found
        assertThat(deleteResponse.statusCode()).isEqualTo(404);
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

    private Long 냉장고_생성_후_ID_반환(String accessToken, String nickname, String model) {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("nickname", nickname, "model", model))
                .when().post("/refrigerators")
                .then().extract();
        return response.jsonPath().getLong("refrigeratorId");
    }
}
