package kr.co.growmeal.common;

import kr.co.growmeal.auth.exception.DuplicateEmailException;
import kr.co.growmeal.auth.exception.DuplicatePhoneNumberException;
import kr.co.growmeal.auth.exception.InvalidCredentialsException;
import kr.co.growmeal.auth.exception.InvalidTokenException;
import kr.co.growmeal.auth.exception.InvalidVerificationCodeException;
import kr.co.growmeal.auth.exception.PhoneNotVerifiedException;
import kr.co.growmeal.auth.exception.VerificationCodeExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({InvalidTokenException.class, InvalidCredentialsException.class})
    public ResponseEntity<Void> handleUnauthorizedException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicatePhoneNumberException.class})
    public ResponseEntity<Void> handleConflictException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({
        PhoneNotVerifiedException.class,
        VerificationCodeExpiredException.class,
        InvalidVerificationCodeException.class
    })
    public ResponseEntity<Void> handleBadRequestException(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
