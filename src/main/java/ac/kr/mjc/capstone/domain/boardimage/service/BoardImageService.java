package ac.kr.mjc.capstone.domain.boardimage.service;

import ac.kr.mjc.capstone.domain.boardimage.dto.BoardImageResponse;
import ac.kr.mjc.capstone.domain.boardimage.entity.BoardImageEntity;
import ac.kr.mjc.capstone.domain.boardimage.repository.BoardImageRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardImageService {

    private final BoardImageRepository boardImageRepository;

    @Value("${file.upload-dir:uploads/board-images}")
    private String uploadDir;

    // 이미지 업로드
    @Transactional
    public BoardImageResponse uploadImage(MultipartFile file) {
        // 파일이 비어있는지 확인
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 파일 형식 검증 (이미지만 허용)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        try {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 원본 파일명과 확장자 추출
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 고유한 파일명 생성 (UUID)
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // 파일 저장
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // DB에 저장
            BoardImageEntity imageEntity = BoardImageEntity.builder()
                    .fileName(originalFileName)
                    .filePath(uniqueFileName)
                    .build();

            BoardImageEntity savedImage = boardImageRepository.save(imageEntity);
            log.info("Image uploaded: imageId={}, fileName={}", savedImage.getImageId(), originalFileName);

            return BoardImageResponse.from(savedImage);

        } catch (IOException e) {
            log.error("Failed to upload image", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    // 이미지 조회
    @Transactional(readOnly = true)
    public Resource loadImage(Long imageId) {
        // DB에서 이미지 정보 조회
        BoardImageEntity imageEntity = boardImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        try {
            // 파일 경로 생성
            Path filePath = Paths.get(uploadDir).resolve(imageEntity.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // 파일 존재 여부 확인
            if (resource.exists() && resource.isReadable()) {
                log.info("Image loaded: imageId={}, fileName={}", imageId, imageEntity.getFileName());
                return resource;
            } else {
                throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            log.error("Failed to load image: imageId={}", imageId, e);
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    // 이미지 정보 조회
    @Transactional(readOnly = true)
    public BoardImageResponse getImageInfo(Long imageId) {
        BoardImageEntity imageEntity = boardImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        return BoardImageResponse.from(imageEntity);
    }

    // 이미지 삭제
    @Transactional
    public void deleteImage(Long imageId) {
        BoardImageEntity imageEntity = boardImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        try {
            // 파일 시스템에서 파일 삭제
            Path filePath = Paths.get(uploadDir).resolve(imageEntity.getFilePath()).normalize();
            Files.deleteIfExists(filePath);

            // DB에서 삭제
            boardImageRepository.delete(imageEntity);
            log.info("Image deleted: imageId={}, fileName={}", imageId, imageEntity.getFileName());

        } catch (IOException e) {
            log.error("Failed to delete image file: imageId={}", imageId, e);
            // 파일 삭제 실패해도 DB에서는 삭제
            boardImageRepository.delete(imageEntity);
        }
    }
}
