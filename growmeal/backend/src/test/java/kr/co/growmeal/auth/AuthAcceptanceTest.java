package kr.co.growmeal.auth;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kr.co.growmeal.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class AuthAcceptanceTest {

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

    @Nested
    @DisplayName("POST /auth/register, /auth/login - 회원가입 및 로그인")
    class AuthRegisterAndLoginTest {

        // Given: 전화번호 인증 후 회원가입
        // When: 로그인
        // Then: 성공적으로 로그인
        @Test
        @DisplayName("회원가입 후 로그인 성공")
        void 회원가입_후_로그인_성공() {
            // Given: 전화번호 인증
            String email = "test@example.com";
            String phoneNumber = "01012345678";
            String password = "Test123!@#";

            // 인증번호 요청
            ExtractableResponse<Response> sendCodeResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("phoneNumber", phoneNumber))
                    .when().post("/auth/phone-verifications")
                    .then().extract();

            assertThat(sendCodeResponse.statusCode()).isEqualTo(200);

            // Redis에서 인증코드 조회 (테스트용)
            String verificationCode = redisTemplate.opsForValue().get("phone:verification:" + phoneNumber);
            assertThat(verificationCode).isNotNull();

            // 인증번호 확인
            ExtractableResponse<Response> confirmCodeResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "phoneNumber", phoneNumber,
                            "code", verificationCode))
                    .when().post("/auth/phone-verifications/confirm")
                    .then().extract();

            assertThat(confirmCodeResponse.statusCode()).isEqualTo(200);

            // Given: 회원가입
            ExtractableResponse<Response> registerResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "email", email,
                            "phoneNumber", phoneNumber,
                            "password", password,
                            "name", "테스트",
                            "role", "MOM"))
                    .when().post("/auth/register")
                    .then().extract();

            assertThat(registerResponse.statusCode()).isEqualTo(201);

            // When: 로그인
            ExtractableResponse<Response> loginResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "email", email,
                            "password", password))
                    .when().post("/auth/login")
                    .then().extract();

            // Then: 성공적으로 로그인
            assertThat(loginResponse.statusCode()).isEqualTo(200);
            assertThat(loginResponse.jsonPath().getString("data.accessToken")).isNotBlank();
        }
    }

    @Nested
    @DisplayName("POST /auth/refresh - 토큰 갱신")
    class AuthRefreshTest {

        // Given: 회원가입 및 로그인하여 refresh token 획득
        // When: refresh token으로 토큰 갱신 요청
        // Then: 새로운 access token 발급
        @Test
        @DisplayName("유효한 refresh token으로 토큰 갱신 성공")
        void 유효한_리프레시_토큰으로_토큰_갱신_성공() {
            // Given: 회원가입 및 로그인하여 refresh token 획득
            String email = "refresh@example.com";
            String phoneNumber = "01099998888";
            String password = "Test123!@#";

            회원가입(email, phoneNumber, password);
            ExtractableResponse<Response> loginResponse = 로그인(email, password);
            String refreshToken = loginResponse.jsonPath().getString("data.refreshToken");

            // When: refresh token으로 토큰 갱신 요청
            ExtractableResponse<Response> refreshResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("refreshToken", refreshToken))
                    .when().post("/auth/refresh")
                    .then().extract();

            // Then: 새로운 access token 발급
            assertThat(refreshResponse.statusCode()).isEqualTo(200);
            assertThat(refreshResponse.jsonPath().getString("data.accessToken")).isNotBlank();
            assertThat(refreshResponse.jsonPath().getInt("data.expiresIn")).isEqualTo(900);
        }

        // Given: 유효하지 않은 refresh token
        // When: 토큰 갱신 요청
        // Then: 401 에러
        @Test
        @DisplayName("유효하지 않은 refresh token으로 토큰 갱신 실패")
        void 유효하지_않은_리프레시_토큰으로_토큰_갱신_실패() {
            // Given: 유효하지 않은 refresh token
            String invalidRefreshToken = "invalid.refresh.token";

            // When: 토큰 갱신 요청
            ExtractableResponse<Response> refreshResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("refreshToken", invalidRefreshToken))
                    .when().post("/auth/refresh")
                    .then().extract();

            // Then: 401 에러
            assertThat(refreshResponse.statusCode()).isEqualTo(401);
        }

        // Given: 빈 refresh token
        // When: 토큰 갱신 요청
        // Then: 400 에러
        @Test
        @DisplayName("빈 refresh token으로 토큰 갱신 실패")
        void 빈_리프레시_토큰으로_토큰_갱신_실패() {
            // Given: 빈 refresh token
            // When: 토큰 갱신 요청
            ExtractableResponse<Response> refreshResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("refreshToken", ""))
                    .when().post("/auth/refresh")
                    .then().extract();

            // Then: 400 에러
            assertThat(refreshResponse.statusCode()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("POST /auth/logout - 로그아웃")
    class AuthLogoutTest {

        // Given: 회원가입 및 로그인
        // When: 로그아웃
        // Then: 성공
        @Test
        @DisplayName("로그아웃 성공")
        void 로그아웃_성공() {
            // Given: 회원가입 및 로그인
            String email = "logout@example.com";
            String phoneNumber = "01077776666";
            String password = "Test123!@#";

            회원가입(email, phoneNumber, password);
            ExtractableResponse<Response> loginResponse = 로그인(email, password);
            String refreshToken = loginResponse.jsonPath().getString("data.refreshToken");

            // When: 로그아웃
            ExtractableResponse<Response> logoutResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("refreshToken", refreshToken))
                    .when().post("/auth/logout")
                    .then().extract();

            // Then: 성공
            assertThat(logoutResponse.statusCode()).isEqualTo(200);
            assertThat(logoutResponse.jsonPath().getString("data.message")).isEqualTo("ok");
        }
    }

    @Nested
    @DisplayName("GET /auth/me - 내 프로필 조회")
    class AuthMeTest {

        // Given: 회원가입 및 로그인
        // When: 내 프로필 조회
        // Then: 프로필 정보 반환
        @Test
        @DisplayName("내 프로필 조회 성공")
        void 내_프로필_조회_성공() {
            // Given: 회원가입 및 로그인
            String email = "me@example.com";
            String phoneNumber = "01055554444";
            String password = "Test123!@#";
            String name = "테스트유저";

            회원가입WithName(email, phoneNumber, password, name);
            ExtractableResponse<Response> loginResponse = 로그인(email, password);
            String accessToken = loginResponse.jsonPath().getString("data.accessToken");

            // When: 내 프로필 조회
            ExtractableResponse<Response> meResponse = RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/auth/me")
                    .then().extract();

            // Then: 프로필 정보 반환
            assertThat(meResponse.statusCode()).isEqualTo(200);
            assertThat(meResponse.jsonPath().getString("data.userId")).isNotBlank();
            assertThat(meResponse.jsonPath().getString("data.name")).isEqualTo(name);
            assertThat(meResponse.jsonPath().getString("data.role")).isEqualTo("MOM");
        }

        @Test
        @DisplayName("아기 등록 후 내 프로필 조회 시 babyId가 반환된다")
        void 아기_등록_후_내_프로필에_babyId_반환() {
            // Given: 회원가입, 로그인, 아기 등록
            String email = "mebaby@example.com";
            String phoneNumber = "01066667777";
            String password = "Test123!@#";

            회원가입WithName(email, phoneNumber, password, "엄마");
            ExtractableResponse<Response> loginResponse = 로그인(email, password);
            String accessToken = loginResponse.jsonPath().getString("data.accessToken");

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(Map.of(
                            "name", "하율",
                            "birthDate", "2024-01-15",
                            "allergies", List.of("땅콩")))
                    .when().post("/babies");

            // When: 내 프로필 조회
            ExtractableResponse<Response> meResponse = RestAssured.given()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/auth/me")
                    .then().extract();

            // Then: babyId가 반환된다
            assertThat(meResponse.statusCode()).isEqualTo(200);
            assertThat(meResponse.jsonPath().getString("data.babyId")).isNotNull();
        }

        // Given: 인증 토큰 없음
        // When: 내 프로필 조회
        // Then: 401 에러
        @Test
        @DisplayName("인증 없이 내 프로필 조회 실패")
        void 인증_없이_내_프로필_조회_실패() {
            // Given: 인증 토큰 없음

            // When: 내 프로필 조회
            ExtractableResponse<Response> meResponse = RestAssured.with()
                    .when().get("/auth/me")
                    .then().log().all().extract();

            // Then: 401 에러
            assertThat(meResponse.statusCode()).isEqualTo(401);
        }
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

    private void 회원가입WithName(String email, String phoneNumber, String password, String name) {
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
                        "name", name,
                        "role", "MOM"))
                .when().post("/auth/register");
    }
}
