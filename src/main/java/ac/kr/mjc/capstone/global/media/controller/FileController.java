package ac.kr.mjc.capstone.global.media.controller;

import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import ac.kr.mjc.capstone.global.media.entity.ImageUsageType;
import ac.kr.mjc.capstone.global.media.service.FileService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLConnection;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    /**
     * 이미지 업로드
     * POST /api/board-images/upload
     * usageType 소문자로 입력 가능
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ImageFileResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("usageType") ImageUsageType usageType) {
        ImageFileResponse response = fileService.uploadImage(file, usageType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 이미지 조회 (파일 다운로드)
     * GET /api/board-images/{imageId}
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId) {
        Resource resource = fileService.loadImage(imageId);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 2️⃣ 파일 이름에서 MIME 타입 추출
        String filename = resource.getFilename();
        String contentType;

        if (filename != null) {
            // 확장자 기반으로 Content-Type 추출
            contentType = URLConnection.guessContentTypeFromName(filename);
        } else {
            contentType = null;
        }

        // 3️⃣ MIME 타입이 추출되지 않으면 fallback
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))// 기본값, 실제로는 파일 타입에 따라 동적으로 설정 가능
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * 이미지 정보 조회
     * GET /api/board-images/{imageId}/info
     */
    @GetMapping("/{imageId}/info")
    public ResponseEntity<ApiResponse<ImageFileResponse>> getImageInfo(@PathVariable Long imageId) {
        ImageFileResponse response = fileService.getImageInfo(imageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 이미지 삭제
     * DELETE /api/board-images/{imageId}
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        fileService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
