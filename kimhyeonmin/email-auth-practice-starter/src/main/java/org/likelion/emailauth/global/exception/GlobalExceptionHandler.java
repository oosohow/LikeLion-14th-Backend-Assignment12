package org.likelion.emailauth.global.exception;

import org.likelion.emailauth.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<ApiResponse> handleEmailVerificationException(EmailVerificationException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.fail(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ApiResponse.fail(message);
    }

    @ExceptionHandler(MailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleMailException() {
        return ApiResponse.fail("인증 메일 전송에 실패했습니다.");
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleUnsupportedOperationException(UnsupportedOperationException exception) {
        return ApiResponse.fail(exception.getMessage());
    }
}
