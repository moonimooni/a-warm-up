package kr.co.growmeal.auth.ui;

import jakarta.validation.Valid;
import kr.co.growmeal.auth.ui.dto.request.LoginRequest;
import kr.co.growmeal.auth.ui.dto.request.LogoutRequest;
import kr.co.growmeal.auth.ui.dto.request.PhoneVerificationConfirmRequest;
import kr.co.growmeal.auth.ui.dto.request.PhoneVerificationRequest;
import kr.co.growmeal.auth.ui.dto.request.RegisterRequest;
import kr.co.growmeal.auth.ui.dto.request.TokenRefreshRequest;
import kr.co.growmeal.auth.ui.dto.response.LoginResponse;
import kr.co.growmeal.auth.ui.dto.response.LogoutResponse;
import kr.co.growmeal.auth.ui.dto.response.MeResponse;
import kr.co.growmeal.auth.ui.dto.response.RegisterResponse;
import kr.co.growmeal.auth.ui.dto.response.TokenRefreshResponse;
import kr.co.growmeal.auth.application.AuthService;
import kr.co.growmeal.auth.application.PhoneVerificationService;
import kr.co.growmeal.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/phone-verifications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendVerificationCode(@Valid @RequestBody PhoneVerificationRequest request) {
        phoneVerificationService.sendVerificationCode(request.phoneNumber());
        return ResponseEntity.ok(ApiResponse.ok(Map.of()));
    }

    @PostMapping("/phone-verifications/confirm")
    public ResponseEntity<ApiResponse<Map<String, Object>>> confirmVerificationCode(@Valid @RequestBody PhoneVerificationConfirmRequest request) {
        phoneVerificationService.verifyCode(request.phoneNumber(), request.code());
        return ResponseEntity.ok(ApiResponse.ok(Map.of()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(@Valid @RequestBody LogoutRequest request) {
        LogoutResponse response = authService.logout(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> getMe(Principal principal) {
        MeResponse response = authService.getMe(principal.getName());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
