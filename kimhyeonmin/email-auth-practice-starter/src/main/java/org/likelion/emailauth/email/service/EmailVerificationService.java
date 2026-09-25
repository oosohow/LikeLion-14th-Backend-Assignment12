package org.likelion.emailauth.email.service;

import lombok.RequiredArgsConstructor;
import org.likelion.emailauth.email.entity.EmailVerification;
import org.likelion.emailauth.email.repository.EmailVerificationRepository;
import org.likelion.emailauth.global.exception.EmailVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    // TODO: 인증번호가 몇 분 동안 유효한지 나타내는 상수입니다.
    private static final long VERIFICATION_CODE_EXPIRATION_MINUTES = 5;

    // *** [과제] 숫자 6자리 대신 영문 대문자 6자리를 사용하도록 변경 ***
    // 인증번호를 만들 때 사용할 글자 후보 (A부터 Z까지)
    private static final String VERIFICATION_CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // 인증번호 길이(자리수)
    private static final int VERIFICATION_CODE_LENGTH = 6;

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;

    // *** [버그 수정] 보내는 사람(From) 주소가 없으면 Gmail이 에러 없이 메일을 조용히 버리는 문제가 있어 추가 ***
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public void sendVerificationCode(String email) {
        String verificationCode = generateVerificationCode();
        EmailVerification emailVerification = saveOrUpdateEmailVerification(email, verificationCode);
        sendEmail(emailVerification.getEmail(), verificationCode);
    }

    @Transactional
    public void verifyCode(String email, String code) {
        // TODO: 이메일에 해당하는 인증정보를 DB에서 조회합니다.
        // TODO: 인증정보가 없다면 인증 요청 정보를 찾을 수 없다는 예외를 발생시킵니다.
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email).orElseThrow(()->new EmailVerificationException("인증 요청 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        // TODO: 인증번호의 만료 시간이 현재 시간보다 이전인지 확인합니다.
        if (emailVerification.getExpiresAt().isBefore(LocalDateTime.now().minusMinutes(VERIFICATION_CODE_EXPIRATION_MINUTES))) {
            throw new EmailVerificationException("인증번호가 만료되었습니다", HttpStatus.BAD_REQUEST);
        }
        // TODO: 사용자가 입력한 인증번호와 DB에 저장된 인증번호를 비교합니다.
        if (!emailVerification.getVerificationCode().equals(code)) {
            throw new EmailVerificationException("인증번호가 올바르지 않습니다", HttpStatus.BAD_REQUEST);
        }
        // TODO: 인증에 성공하면 사용한 인증정보를 삭제해 같은 인증번호를 다시 사용할 수 없게 합니다.
        emailVerificationRepository.delete(emailVerification);
    }

    private String generateVerificationCode() {
        // TODO: 사용자의 이메일로 전송할 랜덤 인증번호를 생성합니다.
        // TODO: 영문 대문자 후보 중에서 6자리 인증번호가 만들어지도록 구현합니다.

        // *** 기존: SecureRandom.nextInt()로 6자리 숫자를 만들던 부분 ***
        // *** [과제] 반복문으로 A~Z 중 하나씩 뽑아서 6글자를 이어 붙이는 방식으로 수정 ***
        SecureRandom random = new SecureRandom();

        // 완성된 인증번호를 담을 빈 문자열
        String verificationCode = "";

        // 6번 반복하면서 글자를 한 개씩 뽑아 이어서
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            // 0 ~ 25 사이의 랜덤한 숫자를 뽑는다 (A=0, B=1, ..., Z=25)
            int randomIndex = random.nextInt(VERIFICATION_CODE_CHARACTERS.length());
            // 뽑은 숫자에 해당하는 글자 한 개를 꺼낸다
            char randomLetter = VERIFICATION_CODE_CHARACTERS.charAt(randomIndex);
            // 꺼낸 글자를 인증번호 뒤에 붙인다
            verificationCode = verificationCode + randomLetter;
        }

        return verificationCode;
    }

    private EmailVerification saveOrUpdateEmailVerification(String email, String verificationCode) {
        // TODO: 현재 시간에 인증번호 유효시간을 더해 만료 시간을 계산합니다.
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(VERIFICATION_CODE_EXPIRATION_MINUTES);
        // TODO: 해당 이메일로 저장된 기존 인증정보가 있는지 Repository로 조회합니다.
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email).orElseGet(()->EmailVerification.create(email, verificationCode, expiresAt));
        // TODO: 기존 데이터가 있다면 새로운 인증번호와 만료 시간으로 갱신합니다.
        emailVerification.updateVerificationCode(verificationCode, expiresAt);
        // TODO: 기존 데이터가 없다면 Entity의 생성 메서드로 새로운 인증정보를 만듭니다.
        return emailVerificationRepository.save(emailVerification);
        // TODO: 생성 또는 갱신된 인증정보를 DB에 저장하고 반환합니다.
    }

    private void sendEmail(String email, String verificationCode) {
        // TODO: SimpleMailMessage 객체를 생성합니다.
        SimpleMailMessage message = new SimpleMailMessage();
        // *** [버그 수정] 보내는 사람 주소를 명시적으로 설정 ***
        message.setFrom(fromEmail);
        // TODO: 인증번호를 받을 수신 이메일 주소를 설정합니다.
        message.setTo(email);
        // TODO: 메일 제목을 설정합니다.
        message.setSubject("[멋사] 이메일 인증번호");
        // TODO: 인증번호와 유효시간이 포함된 메일 본문을 작성합니다.
        message.setText("인증번호는" + verificationCode + "입니다\n" + "인증번호는" + VERIFICATION_CODE_EXPIRATION_MINUTES + "분 동안 유효합니다");
        // TODO: JavaMailSender를 사용해 Gmail SMTP로 실제 메일을 전송합니다.
        javaMailSender.send(message);
    }
}
