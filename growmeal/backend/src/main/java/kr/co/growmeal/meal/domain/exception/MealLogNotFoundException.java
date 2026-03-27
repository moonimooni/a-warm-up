package kr.co.growmeal.meal.domain.exception;

public class MealLogNotFoundException extends RuntimeException {
    public MealLogNotFoundException() {
        super("해당 끼니 기록을 찾을 수 없습니다.");
    }
}
