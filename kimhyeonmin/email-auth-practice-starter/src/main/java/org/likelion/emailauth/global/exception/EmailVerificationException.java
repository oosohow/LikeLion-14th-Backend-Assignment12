package org.likelion.emailauth.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailVerificationException extends RuntimeException {

    private final HttpStatus status;

    public EmailVerificationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
