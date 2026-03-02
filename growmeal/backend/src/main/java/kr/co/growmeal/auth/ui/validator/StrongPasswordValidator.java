package kr.co.growmeal.auth.ui.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    // 8자 이상, 알파벳 + 숫자 + 특수문자 조합
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+])[A-Za-z\\d!@#$%^&*()\\-_=+]{8,}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(value).matches();
    }
}
