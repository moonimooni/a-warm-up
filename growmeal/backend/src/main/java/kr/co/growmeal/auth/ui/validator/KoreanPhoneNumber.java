package kr.co.growmeal.auth.ui.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KoreanPhoneNumberValidator.class)
public @interface KoreanPhoneNumber {
    String message() default "올바른 한국식 핸드폰 번호가 아닙니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
