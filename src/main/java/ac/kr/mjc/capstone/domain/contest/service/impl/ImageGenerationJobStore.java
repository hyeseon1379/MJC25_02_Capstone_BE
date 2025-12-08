package ac.kr.mjc.capstone.domain.contest.service.impl;

import ac.kr.mjc.capstone.domain.contest.dto.ImageGenerationJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 이미지 생성 작업 상태를 메모리에 저장하는 저장소
 * ConcurrentHashMap을 사용하여 Thread-safe하게 관리
 */
@Slf4j
@Component
public class ImageGenerationJobStore {

    private final Map<String, ImageGenerationJob> jobs = new ConcurrentHashMap<>();
    
    // Contest별로 진행 중인 작업이 있는지 확인용
    private final Map<Long, String> contestToJobId = new ConcurrentHashMap<>();

    /**
     * 작업 저장
     */
    public void save(ImageGenerationJob job) {
        jobs.put(job.getJobId(), job);
        contestToJobId.put(job.getContestId(), job.getJobId());
        log.debug("작업 저장: jobId={}, contestId={}", job.getJobId(), job.getContestId());
    }

    /**
     * 작업 조회
     */
    public Optional<ImageGenerationJob> findByJobId(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    /**
     * Contest에 진행 중인 작업이 있는지 확인
     */
    public Optional<ImageGenerationJob> findActiveJobByContestId(Long contestId) {
        String jobId = contestToJobId.get(contestId);
        if (jobId == null) {
            return Optional.empty();
        }
        
        ImageGenerationJob job = jobs.get(jobId);
        if (job == null) {
            contestToJobId.remove(contestId);
            return Optional.empty();
        }
        
        // 완료되거나 실패한 작업은 "진행 중"이 아님
        if (job.getStatus() == ImageGenerationJob.Status.COMPLETED 
                || job.getStatus() == ImageGenerationJob.Status.FAILED) {
            return Optional.empty();
        }
        
        return Optional.of(job);
    }

    /**
     * 작업 완료 시 contestToJobId에서 제거
     */
    public void markCompleted(String jobId) {
        ImageGenerationJob job = jobs.get(jobId);
        if (job != null) {
            contestToJobId.remove(job.getContestId());
        }
    }

    /**
     * 오래된 작업 정리 (1시간 지난 완료/실패 작업)
     * 매 30분마다 실행
     */
    @Scheduled(fixedRate = 1800000) // 30분
    public void cleanupOldJobs() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        jobs.entrySet().removeIf(entry -> {
            ImageGenerationJob job = entry.getValue();
            boolean shouldRemove = job.getCompletedAt() != null 
                    && job.getCompletedAt().isBefore(oneHourAgo);
            
            if (shouldRemove) {
                contestToJobId.remove(job.getContestId());
                log.debug("오래된 작업 정리: jobId={}", job.getJobId());
            }
            return shouldRemove;
        });
    }

    /**
     * 현재 저장된 작업 수 (디버깅/모니터링용)
     */
    public int getJobCount() {
        return jobs.size();
    }
}
