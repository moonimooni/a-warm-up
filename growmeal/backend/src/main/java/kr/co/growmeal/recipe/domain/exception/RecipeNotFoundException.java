package kr.co.growmeal.recipe.domain.exception;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException() {
        super("해당 레시피를 찾을 수 없습니다.");
    }
}
