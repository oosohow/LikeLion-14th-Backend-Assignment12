package org.likelion.emailauth.email.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private EmailVerification(String email, String verificationCode, LocalDateTime expiresAt) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }

    public static EmailVerification create(String email, String verificationCode, LocalDateTime expiresAt) {
        return new EmailVerification(email, verificationCode, expiresAt);
    }

    public void updateVerificationCode(String verificationCode, LocalDateTime expiresAt) {
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }
}
