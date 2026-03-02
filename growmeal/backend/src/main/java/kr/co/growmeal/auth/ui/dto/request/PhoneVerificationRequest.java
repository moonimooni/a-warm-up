package kr.co.growmeal.auth.ui.dto.request;

import jakarta.validation.constraints.NotBlank;
import kr.co.growmeal.auth.ui.validator.KoreanPhoneNumber;

public record PhoneVerificationRequest(
    @NotBlank(message = "전화번호는 필수입니다")
    @KoreanPhoneNumber
    String phoneNumber
) {}
