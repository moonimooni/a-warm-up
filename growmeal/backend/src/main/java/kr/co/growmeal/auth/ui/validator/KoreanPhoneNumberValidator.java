package kr.co.growmeal.auth.ui.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class KoreanPhoneNumberValidator implements ConstraintValidator<KoreanPhoneNumber, String> {

    // 숫자만 입력 (01012345678 형식)
    private static final Pattern KOREAN_PHONE_PATTERN =
        Pattern.compile("^01[016789]\\d{7,8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return KOREAN_PHONE_PATTERN.matcher(value).matches();
    }
}
