package kr.co.growmeal.refrigerator.domain.exception;

public class RefrigeratorNotFoundException extends RuntimeException {
    public RefrigeratorNotFoundException() {
        super("존재하지 않는 냉장고입니다");
    }
}
