package kr.co.growmeal.ingredient;

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
class IngredientAcceptanceTest {

    @LocalServerPort
    private int port;

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
    @DisplayName("전체 재료 목록 조회 성공")
    void 전체_재료_목록_조회_성공() {
        // Given: 재료 마스터 데이터가 존재함 (data.sql로 초기화)

        // When: 인증 없이 GET /ingredients/master 요청
        ExtractableResponse<Response> response = RestAssured.given()
                .when().get("/ingredients/master")
                .then().extract();

        // Then: 200 OK와 함께 재료 목록 반환
        assertThat(response.statusCode()).isEqualTo(200);

        List<Map<String, Object>> ingredients = response.jsonPath().getList("data.ingredients");
        assertThat(ingredients).isNotEmpty();
        assertThat(ingredients).allSatisfy(ingredient -> {
            assertThat(ingredient.get("ingredientId")).isNotNull();
            assertThat(ingredient.get("name")).isNotNull();
            assertThat(ingredient.get("category")).isNotNull();
            assertThat(ingredient.get("nutrients")).isNotNull();
            assertThat(ingredient.get("allergyInfo")).isNotNull();
        });
    }

    @Test
    @DisplayName("재료 검색 성공")
    void 재료_검색_성공() {
        // Given: '두부' 이름을 포함하는 재료 마스터 데이터가 존재함

        // When: GET /ingredients/master/search?q=두부 요청
        ExtractableResponse<Response> response = RestAssured.given()
                .queryParam("q", "두부")
                .when().get("/ingredients/master/search")
                .then().extract();

        // Then: 200 OK와 함께 '두부'를 포함하는 재료 목록 반환
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> names = response.jsonPath().getList("data.ingredients.name");
        assertThat(names).isNotEmpty();
        assertThat(names).allSatisfy(name -> assertThat(name).contains("두부"));
    }
}
