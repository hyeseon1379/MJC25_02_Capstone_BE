package ac.kr.mjc.capstone.auth.scheduler;

import ac.kr.mjc.capstone.auth.repository.EmailVerifyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmailVerifyScheduler {

    private final EmailVerifyRepository repository;
    private static final Logger log = LoggerFactory.getLogger(EmailVerifyScheduler.class);

    @Scheduled(fixedRate = 60000) // 1분(60초)마다 실행
    @Transactional // 삭제는 트랜잭션 내에서 실행되어야 합니다.
    public void clearCode(){
        LocalDateTime now = LocalDateTime.now();

        // 명시적으로 정의된 Bulk DELETE 쿼리 호출
        int deletedCount = repository.deleteByExpiredAtBefore(now);

        // 삭제된 개수가 0보다 클 때만 로그를 남깁니다.
        if (deletedCount > 0) {
            log.info("만료된 이메일 인증 코드 {}개를 삭제했습니다. (기준 시간: {})", deletedCount, now);
        } else {
            // 아무것도 지우지 않았을 때는 로그를 남기지 않거나 DEBUG 레벨로 남길 수 있습니다.
            log.debug("만료된 이메일 인증 코드가 없어 삭제 작업을 건너뜁니다.");
        }
    }
}
