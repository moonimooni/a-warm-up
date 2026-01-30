package kr.co.growmeal.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private static final String VERIFICATION_KEY_PREFIX = "phone:verification:";
    private static final String VERIFIED_KEY_PREFIX = "phone:verified:";

    private final StringRedisTemplate redisTemplate;

    @Value("${phone.verification.expiration-minutes:3}")
    private int expirationMinutes;

    public void sendVerificationCode(String phoneNumber) {
        String code = generateRandomCode();

        // Redis에 인증코드 저장 (만료시간 설정)
        redisTemplate.opsForValue().set(
                VERIFICATION_KEY_PREFIX + phoneNumber,
                code,
                Duration.ofMinutes(expirationMinutes));

        // 더미 SMS 전송 (실제로는 외부 API 호출)
        log.info("[SMS 전송] 전화번호: {}, 인증코드: {}", phoneNumber, code);
    }

    public void verifyCode(String phoneNumber, String code) {
        String storedCode = redisTemplate.opsForValue().get(VERIFICATION_KEY_PREFIX + phoneNumber);

        if (storedCode == null) {
            throw new IllegalArgumentException("인증코드가 만료되었거나 존재하지 않습니다");
        }

        if (!storedCode.equals(code)) {
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다");
        }

        // 인증 완료 표시 (회원가입 시 확인용, 10분간 유효)
        redisTemplate.opsForValue().set(
                VERIFIED_KEY_PREFIX + phoneNumber,
                "true",
                Duration.ofMinutes(10));

        // 인증코드 삭제
        redisTemplate.delete(VERIFICATION_KEY_PREFIX + phoneNumber);
    }

    public boolean isVerified(String phoneNumber) {
        String verified = redisTemplate.opsForValue().get(VERIFIED_KEY_PREFIX + phoneNumber);
        return "true".equals(verified);
    }

    public void clearVerification(String phoneNumber) {
        redisTemplate.delete(VERIFIED_KEY_PREFIX + phoneNumber);
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }
}
