package kr.co.growmeal.common;

import kr.co.growmeal.auth.domain.exception.DuplicateEmailException;
import kr.co.growmeal.auth.domain.exception.DuplicatePhoneNumberException;
import kr.co.growmeal.auth.domain.exception.InvalidCredentialsException;
import kr.co.growmeal.auth.domain.exception.InvalidTokenException;
import kr.co.growmeal.auth.domain.exception.InvalidVerificationCodeException;
import kr.co.growmeal.auth.domain.exception.PhoneNotVerifiedException;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.auth.domain.exception.VerificationCodeExpiredException;
import kr.co.growmeal.refrigerator.domain.exception.RefrigeratorModelNotFoundException;
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
        InvalidVerificationCodeException.class,
        RefrigeratorModelNotFoundException.class
    })
    public ResponseEntity<Void> handleBadRequestException(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
