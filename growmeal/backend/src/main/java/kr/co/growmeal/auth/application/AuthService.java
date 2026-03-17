package kr.co.growmeal.auth.application;

import kr.co.growmeal.auth.domain.exception.DuplicateEmailException;
import kr.co.growmeal.auth.domain.exception.DuplicatePhoneNumberException;
import kr.co.growmeal.auth.domain.exception.InvalidCredentialsException;
import kr.co.growmeal.auth.domain.exception.InvalidTokenException;
import kr.co.growmeal.auth.domain.exception.PhoneNotVerifiedException;
import kr.co.growmeal.auth.ui.dto.request.LoginRequest;
import kr.co.growmeal.auth.ui.dto.request.RegisterRequest;
import kr.co.growmeal.auth.ui.dto.response.LoginResponse;
import kr.co.growmeal.auth.ui.dto.response.LogoutResponse;
import kr.co.growmeal.auth.ui.dto.response.MeResponse;
import kr.co.growmeal.auth.ui.dto.response.RegisterResponse;
import kr.co.growmeal.auth.ui.dto.response.TokenRefreshResponse;
import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PhoneVerificationService phoneVerificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 전화번호 인증 확인
        if (!phoneVerificationService.isVerified(request.phoneNumber())) {
            throw new PhoneNotVerifiedException();
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        // 전화번호 중복 확인
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DuplicatePhoneNumberException();
        }

        // 사용자 저장
        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .phoneNumber(request.phoneNumber())
            .name(request.name())
            .role(request.role())
            .build();

        User savedUser = userRepository.save(user);

        // 인증 정보 삭제
        phoneVerificationService.clearVerification(request.phoneNumber());

        return new RegisterResponse(
            savedUser.getId().toString(),
            savedUser.getName(),
            savedUser.getRole()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        int expiresIn = 900; // 15분 = 900초

        return new LoginResponse(
            user.getId().toString(),
            user.getName(),
            user.getRole(),
            accessToken,
            refreshToken,
            expiresIn
        );
    }

    public TokenRefreshResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        if (!userRepository.existsByEmail(email)) {
            throw new InvalidTokenException();
        }

        String newAccessToken = jwtTokenProvider.generateToken(email);
        int expiresIn = 900; // 15분 = 900초

        return new TokenRefreshResponse(newAccessToken, expiresIn);
    }

    @Transactional
    public LogoutResponse logout(String refreshToken) {
        // Refresh Token 무효화 로직
        // TODO: Redis 또는 DB에 블랙리스트로 저장하여 해당 토큰 무효화
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }
        // 실제 구현 시: refreshTokenRepository.delete(refreshToken);
        return LogoutResponse.ok();
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);

        // TODO: USER_BABY 테이블에서 babyId 조회
        return new MeResponse(
            user.getId().toString(),
            user.getName(),
            user.getRole(),
            null
        );
    }
}
