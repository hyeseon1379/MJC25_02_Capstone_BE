package ac.kr.mjc.capstone.domain.children.controller;

import ac.kr.mjc.capstone.domain.children.dto.ChildrenRequest;
import ac.kr.mjc.capstone.domain.children.dto.ChildrenResponse;
import ac.kr.mjc.capstone.domain.children.dto.ChildrenUpdateRequest;
import ac.kr.mjc.capstone.domain.children.service.ChildrenService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/children")
@RequiredArgsConstructor
public class ChildrenController {

    private final ChildrenService childrenService;

    @PostMapping
    public ApiResponse<ChildrenResponse> createChild(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChildrenRequest request
    ) {
        ChildrenResponse response = childrenService.insert(request, userId);
        return ApiResponse.success("자녀 등록 성공", response);
    }

    @GetMapping
    public ApiResponse<List<ChildrenResponse>> getAllChildren(
            @AuthenticationPrincipal Long userId
    ) {
        List<ChildrenResponse> responses = childrenService.findAll(userId);
        return ApiResponse.success(responses);
    }

    @GetMapping("/{childId}")
    public ApiResponse<ChildrenResponse> getChild(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId
    ) {
        ChildrenResponse response = childrenService.findOne(childId, userId);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{childId}")
    public ApiResponse<ChildrenResponse> updateChild(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId,
            @Valid @RequestBody ChildrenUpdateRequest request
    ) {
        ChildrenResponse response = childrenService.update(childId, request, userId);
        return ApiResponse.success("자녀 정보 수정 성공", response);
    }

    @DeleteMapping("/{childId}")
    public ApiResponse<Void> deleteChild(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId
    ) {
        childrenService.delete(childId, userId);
        return ApiResponse.success("자녀 삭제 성공");
    }

}
