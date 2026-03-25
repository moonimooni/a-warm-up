package kr.co.growmeal.inventory;

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
class InventoryAcceptanceTest {

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
    @DisplayName("로그인 후 인벤토리 목록 조회 성공")
    void 인벤토리_목록_조회_성공() {
        // given
        String email = "inventory-list@example.com";
        String phoneNumber = "01012121212";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");

        // when
        ExtractableResponse<Response> listResponse = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .when().get("/inventory")
            .then().extract();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(200);
        List<Map<String, Object>> inventory = listResponse.jsonPath().getList("data.inventory");
        assertThat(inventory).isEmpty();
    }

    @Test
    @DisplayName("로그인 후 인벤토리 추가 성공")
    void 인벤토리_추가_성공() {
        // given
        String email = "inventory-create@example.com";
        String phoneNumber = "01013131313";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");
        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // when
        ExtractableResponse<Response> createResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", "당근",
                "type", "INGREDIENT",
                "refrigeratorId", refrigeratorId,
                "compartmentId", "냉장_우_1단",
                "expiresAt", "2026-03-10"
            ))
            .when().post("/inventory")
            .then().extract();

        // then
        assertThat(createResponse.statusCode()).isEqualTo(201);
        assertThat(createResponse.jsonPath().getLong("data.itemId")).isNotNull();
        assertThat(createResponse.jsonPath().getString("data.name")).isEqualTo("당근");
        assertThat(createResponse.jsonPath().getString("data.type")).isEqualTo("INGREDIENT");
        assertThat(createResponse.jsonPath().getLong("data.refrigeratorId")).isEqualTo(refrigeratorId);
        assertThat(createResponse.jsonPath().getString("data.compartmentId")).isEqualTo("냉장_우_1단");
        assertThat(createResponse.jsonPath().getString("data.expiresAt")).isEqualTo("2026-03-10");
        assertThat(createResponse.jsonPath().getList("data.nutrients")).isNotNull();
        assertThat(createResponse.jsonPath().getList("data.allergyInfo")).isNotNull();
    }

    @Test
    @DisplayName("인증 없이 인벤토리 목록 조회 시 401 에러")
    void 인증_없이_인벤토리_목록_조회_실패() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given()
            .when().get("/inventory")
            .then().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("다른 사용자 냉장고로 인벤토리 추가 시 404 에러")
    void 다른_사용자_냉장고로_인벤토리_추가_실패() {
        // given
        String password = "Test123!@#";

        String ownerEmail = "inventory-owner@example.com";
        String ownerPhone = "01014141414";
        회원가입(ownerEmail, ownerPhone, password);
        String ownerToken = 로그인(ownerEmail, password).jsonPath().getString("data.accessToken");
        Long ownerRefrigeratorId = 냉장고_생성_후_ID_반환(ownerToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        String otherEmail = "inventory-other@example.com";
        String otherPhone = "01015151515";
        회원가입(otherEmail, otherPhone, password);
        String otherToken = 로그인(otherEmail, password).jsonPath().getString("data.accessToken");

        // when
        ExtractableResponse<Response> createResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + otherToken)
            .body(Map.of(
                "name", "당근",
                "type", "INGREDIENT",
                "refrigeratorId", ownerRefrigeratorId,
                "compartmentId", "냉장_우_1단",
                "expiresAt", "2026-03-10"
            ))
            .when().post("/inventory")
            .then().extract();

        // then
        assertThat(createResponse.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("인벤토리 수정 성공 - compartmentId 변경")
    void 인벤토리_수정_성공() {
        // given
        String email = "inventory-update@example.com";
        String phoneNumber = "01017171717";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");
        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");
        Long itemId = 인벤토리_생성_후_ID_반환(accessToken, refrigeratorId, "당근", "INGREDIENT", "bkf_10", "2026-03-10");

        // when
        ExtractableResponse<Response> updateResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of("compartmentId", "bkf_4"))
            .when().put("/inventory/" + itemId)
            .then().extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(200);
        assertThat(updateResponse.jsonPath().getLong("data.itemId")).isEqualTo(itemId);
        assertThat(updateResponse.jsonPath().getString("data.compartmentId")).isEqualTo("bkf_4");
        assertThat(updateResponse.jsonPath().getString("data.name")).isEqualTo("당근");
    }

    @Test
    @DisplayName("존재하지 않는 인벤토리 수정 시 404 에러")
    void 존재하지_않는_인벤토리_수정_실패() {
        // given
        String email = "inventory-update-notfound@example.com";
        String phoneNumber = "01018181818";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");

        // when
        ExtractableResponse<Response> updateResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of("compartmentId", "bkf_4"))
            .when().put("/inventory/99999")
            .then().extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("다른 사용자의 인벤토리 수정 시 404 에러")
    void 다른_사용자_인벤토리_수정_실패() {
        // given
        String password = "Test123!@#";

        String ownerEmail = "inventory-update-owner@example.com";
        String ownerPhone = "01019191919";
        회원가입(ownerEmail, ownerPhone, password);
        String ownerToken = 로그인(ownerEmail, password).jsonPath().getString("data.accessToken");
        Long ownerRefrigeratorId = 냉장고_생성_후_ID_반환(ownerToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");
        Long itemId = 인벤토리_생성_후_ID_반환(ownerToken, ownerRefrigeratorId, "당근", "INGREDIENT", "bkf_10", "2026-03-10");

        String otherEmail = "inventory-update-other@example.com";
        String otherPhone = "01020202020";
        회원가입(otherEmail, otherPhone, password);
        String otherToken = 로그인(otherEmail, password).jsonPath().getString("data.accessToken");

        // when
        ExtractableResponse<Response> updateResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + otherToken)
            .body(Map.of("compartmentId", "bkf_4"))
            .when().put("/inventory/" + itemId)
            .then().extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("유효하지 않은 compartmentId로 인벤토리 추가 시 400 에러")
    void 유효하지_않은_compartmentId로_인벤토리_추가_실패() {
        // given
        String email = "inventory-invalid-compartment@example.com";
        String phoneNumber = "01016161616";
        String password = "Test123!@#";

        회원가입(email, phoneNumber, password);
        String accessToken = 로그인(email, password).jsonPath().getString("data.accessToken");
        Long refrigeratorId = 냉장고_생성_후_ID_반환(accessToken, "주방 냉장고", "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR");

        // when
        ExtractableResponse<Response> createResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", "당근",
                "type", "INGREDIENT",
                "refrigeratorId", refrigeratorId,
                "compartmentId", "유효하지_않은_칸",
                "expiresAt", "2026-03-10"
            ))
            .when().post("/inventory")
            .then().extract();

        // then
        assertThat(createResponse.statusCode()).isEqualTo(400);
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

    private Long 인벤토리_생성_후_ID_반환(String accessToken, Long refrigeratorId, String name, String type, String compartmentId, String expiresAt) {
        ExtractableResponse<Response> response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(Map.of(
                "name", name,
                "type", type,
                "refrigeratorId", refrigeratorId,
                "compartmentId", compartmentId,
                "expiresAt", expiresAt
            ))
            .when().post("/inventory")
            .then().extract();
        return response.jsonPath().getLong("data.itemId");
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
}
