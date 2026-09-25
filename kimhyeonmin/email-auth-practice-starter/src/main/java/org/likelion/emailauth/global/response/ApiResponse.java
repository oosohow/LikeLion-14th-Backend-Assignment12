package org.likelion.emailauth.global.response;

import lombok.Getter;

@Getter
public class ApiResponse {

    private final boolean success;
    private final String message;

    private ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse fail(String message) {
        return new ApiResponse(false, message);
    }
}
