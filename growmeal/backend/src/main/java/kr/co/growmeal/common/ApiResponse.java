package kr.co.growmeal.common;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T>(
    boolean success,
    @JsonInclude(JsonInclude.Include.ALWAYS) T data,
    @JsonInclude(JsonInclude.Include.ALWAYS) ErrorDetail error
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Object> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorDetail(code, message));
    }

    public record ErrorDetail(String code, String message) {
    }
}
