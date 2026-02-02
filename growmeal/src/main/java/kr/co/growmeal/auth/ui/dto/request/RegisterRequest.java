package kr.co.growmeal.auth.ui.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.co.growmeal.auth.ui.validator.KoreanPhoneNumber;
import kr.co.growmeal.auth.ui.validator.StrongPassword;

public record RegisterRequest(
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,

    @NotBlank(message = "전화번호는 필수입니다")
    @KoreanPhoneNumber
    String phoneNumber,

    @NotBlank(message = "비밀번호는 필수입니다")
    @StrongPassword
    String password
) {}
