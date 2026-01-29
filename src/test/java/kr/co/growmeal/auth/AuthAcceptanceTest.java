package kr.co.growmeal.auth;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthAcceptanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("회원가입 후 로그인 성공")
    void 회원가입_후_로그인_성공() {
        // Given: 올바르게 회원가입
        String email = "test@example.com";
        String phoneNumber = "010-1234-5678";
        String password = "Test123!@#";

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
