package ac.kr.mjc.capstone.domain.contest.service.impl;

import ac.kr.mjc.capstone.domain.contest.dto.ContestResultResponse;
import ac.kr.mjc.capstone.domain.contest.dto.ImageGenerationJob;
import ac.kr.mjc.capstone.domain.contest.entity.*;
import ac.kr.mjc.capstone.domain.contest.repository.ContestDetailsRepository;
import ac.kr.mjc.capstone.domain.contest.repository.ContestRepository;
import ac.kr.mjc.capstone.domain.contest.repository.ContestResultRepository;
import ac.kr.mjc.capstone.domain.contest.repository.StoryRepository;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageUsageType;
import ac.kr.mjc.capstone.global.media.repository.ImageFileRepository;
import ac.kr.mjc.capstone.global.util.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestImageService {

    private final ContestRepository contestRepository;
    private final ContestDetailsRepository contestDetailsRepository;
    private final StoryRepository storyRepository;
    private final ContestResultRepository contestResultRepository;
    private final ImageFileRepository imageFileRepository;
    private final GeminiService geminiService;
    private final ImageGenerationJobStore imageJobStore;

    @Value("${file.contest-image-dir:uploads/contest-images}")
    private String contestImageDir;

    /**
     * 비동기 이미지 생성 작업 시작
     * @return jobId
     */
    public String startImageGenerationJob(Long contestId) {
        // 이미 진행 중인 작업이 있는지 확인
        Optional<ImageGenerationJob> existingJob = imageJobStore.findActiveJobByContestId(contestId);
        if (existingJob.isPresent()) {
            log.warn("이미 진행 중인 작업이 있습니다: jobId={}", existingJob.get().getJobId());
            return existingJob.get().getJobId();
        }

        // Contest 존재 여부 확인
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest를 찾을 수 없습니다. ID: " + contestId));

        // 새 작업 생성
        String jobId = UUID.randomUUID().toString();
        ImageGenerationJob job = ImageGenerationJob.builder()
                .jobId(jobId)
                .contestId(contestId)
                .status(ImageGenerationJob.Status.PENDING)
                .progress(0)
                .totalRounds(4)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        imageJobStore.save(job);
        log.info("이미지 생성 작업 생성: jobId={}, contestId={}", jobId, contestId);

        // 비동기로 이미지 생성 시작
        generateContestImagesAsync(jobId, contest);

        return jobId;
    }

    /**
     * 작업 상태 조회
     */
    public Optional<ImageGenerationJob> getJobStatus(String jobId) {
        return imageJobStore.findByJobId(jobId);
    }

    /**
     * 비동기 이미지 생성 메서드
     */
    @Async("imageGenerationExecutor")
    public void generateContestImagesAsync(String jobId, Contest contest) {
        log.info("비동기 이미지 생성 시작: jobId={}, contest={}", jobId, contest.getTitle());
        
        ImageGenerationJob job = imageJobStore.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("작업을 찾을 수 없습니다: " + jobId));

        createDirectoryIfNotExists();
        Round[] rounds = {Round.ROUND_1, Round.ROUND_2, Round.ROUND_3, Round.FINAL};

        try {
            for (int i = 0; i < rounds.length; i++) {
                Round round = rounds[i];
                
                // 현재 라운드 처리 중으로 상태 업데이트
                job.markAsProcessing(round.getDisplayName());
                imageJobStore.save(job);
                
                log.info("라운드 {} 이미지 생성 시작 (jobId={})", round.getDisplayName(), jobId);

                // 재시도 로직 포함 이미지 생성
                ContestResult result = generateImageWithRetry(contest, round);

                if (result != null) {
                    job.addResult(ContestResultResponse.from(result));
                }
                
                job.incrementProgress();
                imageJobStore.save(job);
                
                log.info("라운드 {} 이미지 생성 완료 (progress: {}/{})", 
                        round.getDisplayName(), job.getProgress(), job.getTotalRounds());

                // 다음 라운드를 위한 대기 (마지막 라운드 제외)
                if (i < rounds.length - 1) {
                    sleepInSeconds(30);
                }
            }

            // 모든 작업 완료
            job.markAsCompleted();
            imageJobStore.save(job);
            imageJobStore.markCompleted(jobId);
            log.info("이미지 생성 작업 완료: jobId={}, 생성된 이미지={}", jobId, job.getResults().size());

        } catch (Exception e) {
            log.error("이미지 생성 작업 실패: jobId={}, error={}", jobId, e.getMessage(), e);
            job.markAsFailed(e.getMessage());
            imageJobStore.save(job);
            imageJobStore.markCompleted(jobId);
        }
    }

    /**
     * 재시도 로직이 포함된 이미지 생성 메서드
     */
    private ContestResult generateImageWithRetry(Contest contest, Round round) {
        int maxRetries = 3;
        int retryCount = 0;
        int waitTime = 60;

        while (retryCount < maxRetries) {
            try {
                return generateImageForRound(contest, round);
            } catch (Exception e) {
                retryCount++;
                log.error("라운드 {} 이미지 생성 실패 (시도 {}/{}): {}", 
                        round, retryCount, maxRetries, e.getMessage());

                if (retryCount >= maxRetries) {
                    log.error("최대 재시도 횟수 초과. 해당 라운드 이미지 생성 포기.");
                    return null;
                }

                log.warn("{}초 대기 후 재시도합니다...", waitTime);
                sleepInSeconds(waitTime);
                waitTime *= 2;
            }
        }
        return null;
    }

    /**
     * 특정 라운드의 1등 글로 이미지 생성
     */
    private ContestResult generateImageForRound(Contest contest, Round round) throws IOException {
        // 1. 해당 라운드의 ContestDetails 조회
        ContestDetails contestDetails = contestDetailsRepository.findByContestAndRound(contest, round)
                .orElse(null);

        if (contestDetails == null) {
            log.warn("라운드 {} ContestDetails가 없습니다.", round);
            return null;
        }

        // 2. 해당 라운드에서 투표수 1위 Story 조회
        Story topStory = storyRepository.findTopByContestDetailsOrderByVoteCountDesc(contestDetails)
                .orElse(null);

        if (topStory == null) {
            log.warn("라운드 {} 에 Story가 없습니다.", round);
            return null;
        }

        log.info("라운드 {} 1위 Story: voteCount={}, content={}", 
                round, topStory.getVoteCount(), 
                topStory.getContent().substring(0, Math.min(50, topStory.getContent().length())));

        // 3. Groq으로 프롬프트 생성
        String prompt = geminiService.generateImagePrompt(topStory.getContent());

        // 4. Pollinations로 이미지 생성 (contestId를 seed로 사용하여 스타일 통일)
        Long seed = contest.getContestId() * 1000; // 대회별 고유 seed
        String base64Image = geminiService.generateImage(prompt, seed);

        // 5. Base64 이미지를 파일로 저장
        String fileName = String.format("contest_%d_%s_%s.png", 
                contest.getContestId(), 
                round.name().toLowerCase(),
                UUID.randomUUID().toString().substring(0, 8));
        
        String filePath = saveBase64Image(base64Image, fileName);

        // 6. ImageFileEntity 저장
        ImageFileEntity imageEntity = ImageFileEntity.builder()
                .fileName(fileName)
                .filePath(filePath)
                .usageType(ImageUsageType.CONTEST_RESULT)
                .build();
        imageFileRepository.save(imageEntity);

        // 7. ContestResult 저장
        String title = round.getDisplayName() + " 우승작";
        
        ContestResult existingResult = contestResultRepository.findByContestAndTitle(contest, title)
                .orElse(null);

        ContestResult contestResult;
        if (existingResult != null) {
            existingResult.setImage(imageEntity);
            existingResult.setFinalContent(topStory.getContent());
            contestResult = contestResultRepository.save(existingResult);
        } else {
            contestResult = ContestResult.builder()
                    .contest(contest)
                    .title(title)
                    .finalContent(topStory.getContent())
                    .image(imageEntity)
                    .build();
            contestResult = contestResultRepository.save(contestResult);
        }

        log.info("라운드 {} 이미지 생성 완료: {}", round, filePath);
        return contestResult;
    }

    private String saveBase64Image(String base64Data, String fileName) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        Path filePath = Paths.get(contestImageDir, fileName);
        Files.write(filePath, imageBytes);
        return filePath.toString();
    }

    private void createDirectoryIfNotExists() {
        try {
            Path path = Paths.get(contestImageDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("디렉토리 생성: {}", contestImageDir);
            }
        } catch (IOException e) {
            log.error("디렉토리 생성 실패: {}", e.getMessage());
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }

    private void sleepInSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ========== 기존 동기 방식 (레거시, 필요시 사용) ==========
    
    /**
     * 기존 동기 방식 이미지 생성 (레거시)
     * @deprecated 비동기 방식 사용 권장: startImageGenerationJob()
     */
    @Deprecated
    public List<ContestResult> generateContestImages(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest를 찾을 수 없습니다. ID: " + contestId));

        log.info("Contest 이미지 생성 시작 (동기): {}", contest.getTitle());
        createDirectoryIfNotExists();

        List<ContestResult> results = new ArrayList<>();
        Round[] rounds = {Round.ROUND_1, Round.ROUND_2, Round.ROUND_3, Round.FINAL};

        for (int i = 0; i < rounds.length; i++) {
            Round round = rounds[i];
            ContestResult result = generateImageWithRetry(contest, round);

            if (result != null) {
                results.add(result);
            }

            if (i < rounds.length - 1) {
                sleepInSeconds(30);
            }
        }

        log.info("Contest 이미지 생성 완료 (동기): {} 개 생성됨", results.size());
        return results;
    }
}
