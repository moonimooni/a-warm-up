package kr.co.growmeal.auth;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
    }

    @Test
    @DisplayName("회원가입 후 로그인 성공")
    void 회원가입_후_로그인_성공() {
        // Given: 전화번호 인증
        String email = "test@example.com";
        String phoneNumber = "01012345678";
        String password = "Test123!@#";

        // 인증번호 요청
        var sendCodeResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of("phoneNumber", phoneNumber))
            .when().post("/auth/phone-verifications")
            .then().extract();

        assertThat(sendCodeResponse.statusCode()).isEqualTo(200);

        // Redis에서 인증코드 조회 (테스트용)
        String verificationCode = redisTemplate.opsForValue().get("phone:verification:" + phoneNumber);
        assertThat(verificationCode).isNotNull();

        // 인증번호 확인
        var confirmCodeResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "phoneNumber", phoneNumber,
                "code", verificationCode
            ))
            .when().post("/auth/phone-verifications/confirm")
            .then().extract();

        assertThat(confirmCodeResponse.statusCode()).isEqualTo(200);

        // Given: 회원가입
        var registerResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "email", email,
                "phoneNumber", phoneNumber,
                "password", password
            ))
            .when().post("/auth/register")
            .then().extract();

        assertThat(registerResponse.statusCode()).isEqualTo(201);

        // When: 로그인
        var loginResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "email", email,
                "password", password
            ))
            .when().post("/auth/login")
            .then().extract();

        // Then: 성공적으로 로그인
        assertThat(loginResponse.statusCode()).isEqualTo(200);
        assertThat(loginResponse.jsonPath().getString("accessToken")).isNotBlank();
    }
}
