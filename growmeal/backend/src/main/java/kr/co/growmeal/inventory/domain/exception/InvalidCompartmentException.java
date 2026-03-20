package kr.co.growmeal.inventory.domain.exception;

public class InvalidCompartmentException extends RuntimeException {
    public InvalidCompartmentException() {
        super("유효하지 않은 냉장고 칸입니다");
    }
}
