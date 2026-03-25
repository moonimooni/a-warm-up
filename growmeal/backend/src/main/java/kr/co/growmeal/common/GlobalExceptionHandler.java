package kr.co.growmeal.common;

import kr.co.growmeal.auth.domain.exception.DuplicateEmailException;
import kr.co.growmeal.auth.domain.exception.DuplicatePhoneNumberException;
import kr.co.growmeal.auth.domain.exception.InvalidCredentialsException;
import kr.co.growmeal.auth.domain.exception.InvalidTokenException;
import kr.co.growmeal.auth.domain.exception.InvalidVerificationCodeException;
import kr.co.growmeal.auth.domain.exception.PhoneNotVerifiedException;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.auth.domain.exception.VerificationCodeExpiredException;
import kr.co.growmeal.inventory.domain.exception.InvalidCompartmentException;
import kr.co.growmeal.inventory.domain.exception.InventoryItemNotFoundException;
import kr.co.growmeal.refrigerator.domain.exception.RefrigeratorModelNotFoundException;
import kr.co.growmeal.refrigerator.domain.exception.RefrigeratorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .findFirst()
            .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("UNAUTHORIZED", "이메일 또는 비밀번호가 일치하지 않습니다."));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("INVALID_TOKEN", "유효하지 않은 토큰입니다."));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateEmailException(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."));
    }

    @ExceptionHandler(DuplicatePhoneNumberException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicatePhoneNumberException(DuplicatePhoneNumberException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("DUPLICATE_PHONE_NUMBER", "이미 사용 중인 전화번호입니다."));
    }

    @ExceptionHandler({
        PhoneNotVerifiedException.class,
        VerificationCodeExpiredException.class,
        InvalidVerificationCodeException.class,
        RefrigeratorModelNotFoundException.class,
        InvalidCompartmentException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(RuntimeException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class, RefrigeratorNotFoundException.class, InventoryItemNotFoundException.class})
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("NOT_FOUND", e.getMessage()));
    }
}
