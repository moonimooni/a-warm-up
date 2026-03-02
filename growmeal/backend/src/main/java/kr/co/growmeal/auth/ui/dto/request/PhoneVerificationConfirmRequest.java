package kr.co.growmeal.auth.ui.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.co.growmeal.auth.ui.validator.KoreanPhoneNumber;

public record PhoneVerificationConfirmRequest(
    @NotBlank(message = "전화번호는 필수입니다")
    @KoreanPhoneNumber
    String phoneNumber,

    @NotBlank(message = "인증코드는 필수입니다")
    @Pattern(regexp = "^\\d{6}$", message = "인증코드는 6자리 숫자입니다")
    String code
) {}
