package kr.co.growmeal.auth.exception;

public class VerificationCodeExpiredException extends RuntimeException {

    public VerificationCodeExpiredException() {
        super("인증코드가 만료되었거나 존재하지 않습니다");
    }
}
