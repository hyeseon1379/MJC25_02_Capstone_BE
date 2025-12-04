package ac.kr.mjc.capstone.domain.packaze.controller;

import ac.kr.mjc.capstone.domain.packaze.dto.PackazeResponse;
import ac.kr.mjc.capstone.domain.packaze.entity.Packaze;
import ac.kr.mjc.capstone.domain.packaze.service.PackazeService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Tag(name = "Package", description = "구독 패키지 API")
public class PackazeController {

    private final PackazeService packageService;

    @GetMapping
    @Operation(summary = "전체 패키지 조회", description = "활성화된 모든 구독 패키지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "패키지 목록 조회 성공")
    })
    public ResponseEntity<ApiResponse<List<PackazeResponse>>> getAllPackages() {
        List<Packaze> packages = packageService.getAllActivePackages();
        List<PackazeResponse> response = packages.stream()
                .map(PackazeResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "패키지 상세 조회", description = "패키지 ID로 특정 구독 패키지 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "패키지 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "패키지를 찾을 수 없음")
    })
    public ResponseEntity<ApiResponse<PackazeResponse>> getPackage(
            @Parameter(description = "패키지 ID", required = true, example = "1")
            @PathVariable Long id) {
        Packaze packageItem = packageService.getPackageById(id);
        return ResponseEntity.ok(ApiResponse.success(PackazeResponse.from(packageItem)));
    }
}
