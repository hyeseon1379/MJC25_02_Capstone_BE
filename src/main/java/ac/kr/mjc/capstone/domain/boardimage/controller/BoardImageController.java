package ac.kr.mjc.capstone.domain.boardimage.controller;

import ac.kr.mjc.capstone.domain.boardimage.dto.BoardImageResponse;
import ac.kr.mjc.capstone.domain.boardimage.service.BoardImageService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/board-images")
@RequiredArgsConstructor
public class BoardImageController {

    private final BoardImageService boardImageService;

    /**
     * 이미지 업로드
     * POST /api/board-images/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<BoardImageResponse>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        BoardImageResponse response = boardImageService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 이미지 조회 (파일 다운로드)
     * GET /api/board-images/{imageId}
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId) {
        Resource resource = boardImageService.loadImage(imageId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 기본값, 실제로는 파일 타입에 따라 동적으로 설정 가능
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * 이미지 정보 조회
     * GET /api/board-images/{imageId}/info
     */
    @GetMapping("/{imageId}/info")
    public ResponseEntity<ApiResponse<BoardImageResponse>> getImageInfo(@PathVariable Long imageId) {
        BoardImageResponse response = boardImageService.getImageInfo(imageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 이미지 삭제
     * DELETE /api/board-images/{imageId}
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        boardImageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
