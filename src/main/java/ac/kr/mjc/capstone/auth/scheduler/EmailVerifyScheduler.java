package ac.kr.mjc.capstone.auth.scheduler;

import ac.kr.mjc.capstone.auth.repository.EmailVerifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmailVerifyScheduler {

    private final EmailVerifyRepository repository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void clearCode(){
        repository.deleteByExpiredAtBefore(LocalDateTime.now());
    }
}
