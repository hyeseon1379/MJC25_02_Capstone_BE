package ac.kr.mjc.capstone.global.media.service;

import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageUsageType;
import ac.kr.mjc.capstone.global.media.repository.FileRepository;
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
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload-base-dir:uploads}")
    private String baseUploadDir;

    @Transactional
    public ImageFileResponse uploadImage(MultipartFile file, ImageUsageType imageUsageType) {

        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        try {
            Path uploadPath = Paths.get(baseUploadDir, imageUsageType.getDirectory());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID().toString() + extension;

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            ImageFileEntity imageEntity = ImageFileEntity.builder()
                    .fileName(originalFileName)
                    .filePath(uniqueFileName)
                    .usageType(imageUsageType)
                    .build();

            ImageFileEntity savedImage = fileRepository.save(imageEntity);
            log.info("Image uploaded: imageId={}, fileName={}", savedImage.getImageId(), originalFileName);

            return ImageFileResponse.from(savedImage);

        } catch (IOException e) {
            log.error("Failed to upload image", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public Resource loadImage(Long imageId) {
        ImageFileEntity fileEntity = fileRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        try {
            String domainDirectory = fileEntity.getUsageType().getDirectory();

            Path fullUploadPath = Paths.get(baseUploadDir, domainDirectory);
            Path filePath = fullUploadPath.resolve(fileEntity.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                log.info("Image loaded: imageId={}, fileName={}", imageId, fileEntity.getFileName());
                return resource;
            } else {
                throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            log.error("Failed to load image: imageId={}", imageId, e);
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public ImageFileResponse getImageInfo(Long imageId) {
        ImageFileEntity imageEntity = fileRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        return ImageFileResponse.from(imageEntity);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ImageFileEntity imageEntity = fileRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        try {
            String domainDirectory = imageEntity.getUsageType().getDirectory();
            Path fullUploadPath = Paths.get(baseUploadDir, domainDirectory);
            Path filePath = fullUploadPath.resolve(imageEntity.getFilePath()).normalize();
            Files.deleteIfExists(filePath);

            fileRepository.delete(imageEntity);
            log.info("Image deleted: imageId={}, fileName={}", imageId, imageEntity.getFileName());

        } catch (IOException e) {
            log.error("Failed to delete image file: imageId={}", imageId, e);
            fileRepository.delete(imageEntity);
        }
    }
}
