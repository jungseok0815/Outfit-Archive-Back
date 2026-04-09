package com.fasthub.backend.user.usr.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.usr.dto.ForgotPasswordDto;
import com.fasthub.backend.user.usr.dto.ResetPasswordDto;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenService tokenService;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    // 1단계: 이메일로 재설정 링크 발송
    public void sendResetEmail(ForgotPasswordDto dto) {
        // 이메일(userId)로 사용자 조회 - 존재 여부와 관계없이 동일 응답 (보안)
        authRepository.findByUserId(dto.getEmail()).ifPresent(user -> {
            String token = tokenService.generateToken(user.getUserId());
            String resetLink = baseUrl + "/reset-password?token=" + token;
            sendEmail(user.getUserId(), resetLink);
        });
    }

    // 2단계: 토큰 검증 후 비밀번호 변경
    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        String userId = tokenService.getUserId(dto.getToken());
        if (userId == null) {
            throw new BusinessException(ErrorCode.PASSWORD_RESET_TOKEN_INVALID);
        }

        User user = authRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.update(user.getUserNm(), user.getUserAge(),
                passwordEncoder.encode(dto.getNewPassword()), user.getBio());

        tokenService.deleteToken(dto.getToken());
    }

    private void sendEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[Outfit Archive] 비밀번호 재설정 안내");
            helper.setText(buildEmailHtml(resetLink), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.PASSWORD_RESET_EMAIL_FAIL);
        }
    }

    private String buildEmailHtml(String resetLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 32px; background: #f9f9f9; border-radius: 8px;">
                  <h2 style="color: #222; margin-bottom: 8px;">Outfit Archive</h2>
                  <p style="color: #555;">비밀번호 재설정 요청이 접수되었습니다.</p>
                  <p style="color: #555;">아래 버튼을 클릭하여 10분 이내에 비밀번호를 재설정해주세요.</p>
                  <a href="%s"
                     style="display: inline-block; margin: 24px 0; padding: 12px 28px; background: #222; color: #fff; text-decoration: none; border-radius: 4px; font-size: 15px;">
                    비밀번호 재설정
                  </a>
                  <p style="color: #aaa; font-size: 12px;">본인이 요청하지 않으셨다면 이 메일을 무시하세요.</p>
                </div>
                """.formatted(resetLink);
    }
}
