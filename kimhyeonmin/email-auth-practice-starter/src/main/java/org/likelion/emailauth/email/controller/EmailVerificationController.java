package org.likelion.emailauth.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.likelion.emailauth.email.dto.EmailSendRequest;
import org.likelion.emailauth.email.dto.EmailVerifyRequest;
import org.likelion.emailauth.email.service.EmailVerificationService;
import org.likelion.emailauth.global.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ApiResponse sendVerificationCode(@Valid @RequestBody EmailSendRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ApiResponse.success("인증번호를 전송했습니다.");
    }

    @PostMapping("/verify")
    public ApiResponse verifyCode(@Valid @RequestBody EmailVerifyRequest request) {
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ApiResponse.success("이메일 인증에 성공했습니다.");
    }
}
