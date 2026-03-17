package kr.co.growmeal.auth.ui.dto.response;

public record LogoutResponse(
    String message
) {
    public static LogoutResponse ok() {
        return new LogoutResponse("ok");
    }
}
