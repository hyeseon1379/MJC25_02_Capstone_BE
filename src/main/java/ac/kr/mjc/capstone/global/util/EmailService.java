package ac.kr.mjc.capstone.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${SPRING_MAIL_USERNAME}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            // 네이버 SMTP는 인증된 계정의 실제 이메일을 사용해야 함
            message.setFrom(fromEmail + "@naver.com");

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다");
        }
    }

    /**
     * 구독 완료 확인 이메일 발송
     */
    public void sendSubscriptionConfirmation(String to, String packageName, LocalDateTime endDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(fromEmail + "@naver.com");
            message.setSubject("[책이음] 구독이 완료되었습니다");
            message.setText(String.format(
                    "안녕하세요,\n\n" +
                            "구독이 성공적으로 완료되었습니다.\n\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "구독 상품: %s\n" +
                            "구독 종료일: %s\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "구독 기간 동안 다양한 서비스를 이용해 보세요.\n\n" +
                            "감사합니다.\n" +
                            "책이음 드림",
                    packageName,
                    endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            ));

            mailSender.send(message);
            log.info("구독 확인 이메일 발송 성공 - to: {}", to);
        } catch (Exception e) {
            log.error("구독 확인 이메일 발송 실패 - to: {}, error: {}", to, e.getMessage());
            throw new RuntimeException("구독 확인 이메일 발송에 실패했습니다");
        }
    }

    /**
     * 구독 취소 확인 이메일 발송
     */
    public void sendSubscriptionCancellation(String to, String packageName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(fromEmail + "@naver.com");
            message.setSubject("[책이음] 구독이 취소되었습니다");
            message.setText(String.format(
                    "안녕하세요,\n\n" +
                            "구독이 취소되었습니다.\n\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "취소된 구독 상품: %s\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "다시 이용을 원하시면 언제든지 재구독해 주세요.\n\n" +
                            "감사합니다.\n" +
                            "책이음 드림",
                    packageName
            ));

            mailSender.send(message);
            log.info("구독 취소 이메일 발송 성공 - to: {}", to);
        } catch (Exception e) {
            log.error("구독 취소 이메일 발송 실패 - to: {}, error: {}", to, e.getMessage());
            throw new RuntimeException("구독 취소 이메일 발송에 실패했습니다");
        }
    }
}