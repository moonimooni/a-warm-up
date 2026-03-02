package kr.co.growmeal.auth.exception;

public class PhoneNotVerifiedException extends RuntimeException {

    public PhoneNotVerifiedException() {
        super("전화번호 인증이 완료되지 않았습니다");
    }
}
