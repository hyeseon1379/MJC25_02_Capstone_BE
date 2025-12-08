package ac.kr.mjc.capstone.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "imageGenerationExecutor")
    public Executor imageGenerationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);      // 동시에 2개 작업 처리
        executor.setMaxPoolSize(4);       // 최대 4개까지 확장
        executor.setQueueCapacity(10);    // 대기열 10개
        executor.setThreadNamePrefix("ImageGen-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        
        log.info("이미지 생성용 비동기 Executor 초기화 완료");
        return executor;
    }
}
