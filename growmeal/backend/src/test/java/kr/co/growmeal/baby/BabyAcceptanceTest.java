package kr.co.growmeal.baby;

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
class BabyAcceptanceTest {

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
    @DisplayName("로그인 후 아기 정보를 올바르게 기입하면 성공 리스폰스를 받는다")
    void 로그인_후_아기_정보_등록_성공() {
        // Given: 회원가입 및 로그인
        String email = "parent@example.com";
        String phoneNumber = "01011112222";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("data.accessToken");

        // When: 아기 정보를 올바르게 기입
        ExtractableResponse<Response> createBabyResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", "하율",
                "birthDate", "2024-01-15",
                "allergies", List.of("땅콩", "우유"),
                "heightCm", 75.5,
                "weightKg", 9.5
            ))
            .when().post("/babies")
            .then().extract();

        // Then: 성공 리스폰스를 받는다
        assertThat(createBabyResponse.statusCode()).isEqualTo(201);
        assertThat(createBabyResponse.jsonPath().getLong("data.babyId")).isNotNull();
        assertThat(createBabyResponse.jsonPath().getString("data.name")).isEqualTo("하율");
        assertThat(createBabyResponse.jsonPath().getString("data.birthDate")).isEqualTo("2024-01-15");
        assertThat(createBabyResponse.jsonPath().getList("data.allergies")).containsExactly("땅콩", "우유");
        assertThat(createBabyResponse.jsonPath().getDouble("data.heightCm")).isEqualTo(75.5);
        assertThat(createBabyResponse.jsonPath().getDouble("data.weightKg")).isEqualTo(9.5);
        assertThat(createBabyResponse.jsonPath().getString("data.createdAt")).isNotNull();
    }

    @Test
    @DisplayName("인증 없이 아기 정보 등록 시도하면 401 에러")
    void 인증_없이_아기_정보_등록_실패() {
        // Given: 인증 없이
        // When: 아기 정보 등록 시도
        ExtractableResponse<Response> createBabyResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "name", "하율",
                "birthDate", "2024-01-15",
                "allergies", List.of("땅콩", "우유")
            ))
            .when().post("/babies")
            .then().extract();

        // Then: 401 에러
        assertThat(createBabyResponse.statusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("아기 등록 후 조회하면 등록한 정보가 반환된다")
    void 아기_등록_후_조회_성공() {
        // Given: 회원가입, 로그인, 아기 등록
        String email = "getbaby@example.com";
        String phoneNumber = "01055556666";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("data.accessToken");

        ExtractableResponse<Response> createResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", "도율",
                "birthDate", "2024-06-01",
                "allergies", List.of("우유"),
                "heightCm", 70.0,
                "weightKg", 8.0
            ))
            .when().post("/babies")
            .then().extract();

        Long babyId = createResponse.jsonPath().getLong("data.babyId");

        // When: 아기 조회
        ExtractableResponse<Response> getResponse = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .when().get("/babies/{babyId}", babyId)
            .then().extract();

        // Then: 등록한 정보가 반환된다
        assertThat(getResponse.statusCode()).isEqualTo(200);
        assertThat(getResponse.jsonPath().getLong("data.babyId")).isEqualTo(babyId);
        assertThat(getResponse.jsonPath().getString("data.name")).isEqualTo("도율");
        assertThat(getResponse.jsonPath().getString("data.birthDate")).isEqualTo("2024-06-01");
        assertThat(getResponse.jsonPath().getList("data.allergies")).containsExactly("우유");
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 에러")
    void 필수_필드_누락_시_실패() {
        // Given: 회원가입 및 로그인
        String email = "parent2@example.com";
        String phoneNumber = "01033334444";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        ExtractableResponse<Response> loginResponse = 로그인(email, password);
        String accessToken = loginResponse.jsonPath().getString("data.accessToken");

        // When: 필수 필드(name) 누락
        ExtractableResponse<Response> createBabyResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "birthDate", "2024-01-15"
            ))
            .when().post("/babies")
            .then().extract();

        // Then: 400 에러
        assertThat(createBabyResponse.statusCode()).isEqualTo(400);
    }

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
}
