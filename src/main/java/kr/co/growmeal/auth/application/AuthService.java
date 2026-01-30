package kr.co.growmeal.auth.application;

import kr.co.growmeal.auth.exception.InvalidTokenException;
import kr.co.growmeal.auth.ui.dto.request.LoginRequest;
import kr.co.growmeal.auth.ui.dto.request.RegisterRequest;
import kr.co.growmeal.auth.ui.dto.response.LoginResponse;
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
    public void register(RegisterRequest request) {
        // 전화번호 인증 확인
        if (!phoneVerificationService.isVerified(request.phoneNumber())) {
            throw new IllegalArgumentException("전화번호 인증이 완료되지 않았습니다");
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // 전화번호 중복 확인
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다");
        }

        // 사용자 저장
        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .phoneNumber(request.phoneNumber())
            .build();

        userRepository.save(user);

        // 인증 정보 삭제
        phoneVerificationService.clearVerification(request.phoneNumber());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(email);
        return new LoginResponse(newAccessToken, refreshToken);
    }
}
