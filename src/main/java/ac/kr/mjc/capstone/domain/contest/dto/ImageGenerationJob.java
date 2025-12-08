package ac.kr.mjc.capstone.domain.contest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ImageGenerationJob {

    public enum Status {
        PENDING,      // 대기 중
        PROCESSING,   // 처리 중
        COMPLETED,    // 완료
        FAILED        // 실패
    }

    private String jobId;
    private Long contestId;
    private Status status;
    
    private int progress;         // 완료된 라운드 수
    private int totalRounds;      // 총 라운드 수 (4)
    private String currentRound;  // 현재 처리 중인 라운드
    
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;  // 완료 시간 (정리 스케줄러용)
    
    @Builder.Default
    private List<ContestResultResponse> results = new ArrayList<>();
    
    public void incrementProgress() {
        this.progress++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addResult(ContestResultResponse result) {
        this.results.add(result);
    }
    
    public void markAsProcessing(String round) {
        this.status = Status.PROCESSING;
        this.currentRound = round;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsCompleted() {
        this.status = Status.COMPLETED;
        this.currentRound = null;
        this.updatedAt = LocalDateTime.now();
        this.completedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String errorMessage) {
        this.status = Status.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
        this.completedAt = LocalDateTime.now();
    }
}
