package kr.co.growmeal.refrigerator;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kr.co.growmeal.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig.class)
class RefrigeratorModelAcceptanceTest {

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", EmbeddedRedisConfig::getRedisPort);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    // Given: 냉장고 모델 데이터가 존재함 (data.sql로 초기화)
    // When: 인증 없이 GET /refrigerator-models 요청
    // Then: 200 OK와 함께 모델 목록 반환
    @Test
    @DisplayName("냉장고 모델 목록 조회 성공")
    void 냉장고_모델_목록_조회_성공() {
        // Given: 냉장고 모델 데이터가 존재함 (data.sql로 초기화)

        // When: 인증 없이 GET /refrigerator-models 요청
        ExtractableResponse<Response> response = RestAssured.given()
                .when().get("/refrigerator-models")
                .then().extract();

        // Then: 200 OK와 함께 모델 목록 반환
        assertThat(response.statusCode()).isEqualTo(200);

        List<Map<String, Object>> models = response.jsonPath().getList("models");
        assertThat(models).isNotEmpty();
        assertThat(models).allSatisfy(model -> {
            assertThat(model.get("model")).isNotNull();
            assertThat(model.get("name")).isNotNull();
            assertThat(model).containsKey("imageUrl");
        });
    }
}
