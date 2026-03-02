package kr.co.growmeal.auth.exception;

public class InvalidVerificationCodeException extends RuntimeException {

    public InvalidVerificationCodeException() {
        super("인증코드가 일치하지 않습니다");
    }
}
