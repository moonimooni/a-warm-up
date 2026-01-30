package kr.co.growmeal.auth.ui;

import jakarta.validation.Valid;
import kr.co.growmeal.auth.ui.dto.request.LoginRequest;
import kr.co.growmeal.auth.ui.dto.request.PhoneVerificationConfirmRequest;
import kr.co.growmeal.auth.ui.dto.request.PhoneVerificationRequest;
import kr.co.growmeal.auth.ui.dto.request.RegisterRequest;
import kr.co.growmeal.auth.ui.dto.request.TokenRefreshRequest;
import kr.co.growmeal.auth.ui.dto.response.LoginResponse;
import kr.co.growmeal.auth.application.AuthService;
import kr.co.growmeal.auth.application.PhoneVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/phone-verifications")
    public ResponseEntity<Void> sendVerificationCode(@Valid @RequestBody PhoneVerificationRequest request) {
        phoneVerificationService.sendVerificationCode(request.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/phone-verifications/confirm")
    public ResponseEntity<Void> confirmVerificationCode(@Valid @RequestBody PhoneVerificationConfirmRequest request) {
        phoneVerificationService.verifyCode(request.phoneNumber(), request.code());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tokens/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        LoginResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}
