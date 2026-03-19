package kr.co.growmeal.refrigerator.domain.exception;

public class RefrigeratorModelNotFoundException extends RuntimeException {
    public RefrigeratorModelNotFoundException() {
        super("존재하지 않는 냉장고 모델입니다");
    }
}
