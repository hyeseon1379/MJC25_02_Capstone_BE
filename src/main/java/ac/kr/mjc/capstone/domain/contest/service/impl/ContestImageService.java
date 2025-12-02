package ac.kr.mjc.capstone.domain.contest.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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

    @Value("${file.contest-image-dir:uploads/contest-images}")
    private String contestImageDir;

    /**
     * 특정 Contest의 4개 라운드 1등 글로 이미지 생성
     */
    @Transactional
    public List<ContestResult> generateContestImages(Long contestId) {
        // 1. Contest 조회
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest를 찾을 수 없습니다. ID: " + contestId));

        log.info("Contest 이미지 생성 시작: {}", contest.getTitle());

        // 디렉토리 생성
        createDirectoryIfNotExists();

        List<ContestResult> results = new ArrayList<>();
        Round[] rounds = {Round.ROUND_1, Round.ROUND_2, Round.ROUND_3, Round.FINAL};

        for (Round round : rounds) {
            try {
                ContestResult result = generateImageForRound(contest, round);
                if (result != null) {
                    results.add(result);
                }
            } catch (Exception e) {
                log.error("라운드 {} 이미지 생성 실패: {}", round, e.getMessage());
            }
        }

        log.info("Contest 이미지 생성 완료: {} 개 생성됨", results.size());
        return results;
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
                round, topStory.getVoteCount(), topStory.getContent().substring(0, Math.min(50, topStory.getContent().length())));

        // 3. Gemini로 프롬프트 생성
        String prompt = geminiService.generateImagePrompt(topStory.getContent());

        // 4. Gemini로 이미지 생성
        String base64Image = geminiService.generateImage(prompt);

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
        
        // 이미 존재하는지 확인
        ContestResult existingResult = contestResultRepository.findByContestAndTitle(contest, title)
                .orElse(null);

        ContestResult contestResult;
        if (existingResult != null) {
            // 기존 결과 업데이트
            existingResult.setImage(imageEntity);
            existingResult.setFinalContent(topStory.getContent());
            contestResult = contestResultRepository.save(existingResult);
        } else {
            // 새로 생성
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

    /**
     * Base64 이미지를 파일로 저장
     */
    private String saveBase64Image(String base64Data, String fileName) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        Path filePath = Paths.get(contestImageDir, fileName);
        Files.write(filePath, imageBytes);
        return filePath.toString();
    }

    /**
     * 이미지 저장 디렉토리 생성
     */
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
}
