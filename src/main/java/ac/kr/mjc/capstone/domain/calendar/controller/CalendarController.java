package ac.kr.mjc.capstone.domain.calendar.controller;

import ac.kr.mjc.capstone.domain.calendar.dto.*;
import ac.kr.mjc.capstone.domain.calendar.service.inf.CalendarScheduleService;
import ac.kr.mjc.capstone.domain.calendar.service.inf.CalendarService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "독서 캘린더 API")
public class CalendarController {

    private final CalendarService calendarService;
    private final CalendarScheduleService scheduleService;

    // ==================== 기존 API (하위 호환) ====================

    @GetMapping("/{year}/{month}")
    @Operation(summary = "캘린더 조회 (기존)", description = "월간 캘린더 정보를 조회합니다. (기존 BookDetails 기반)")
    public ApiResponse<List<CalendarResponse>> getMonthlyCalendar(
            @AuthenticationPrincipal Long userId,
            @PathVariable("year") int year,
            @PathVariable("month") int month) {

        List<CalendarResponse> response = calendarService.getMonthlyCalendar(userId, year, month);

        return ApiResponse.success("월간 독서 캘린더 조회 성공", response);
    }

    @GetMapping("/date")
    @Operation(summary = "캘린더 상세 조회 (기존)", description = "특정 날짜의 도서 상세 목록을 조회합니다. (기존 BookDetails 기반)")
    public ApiResponse<CalendarDailyResponse> getDailyRecords(
            @AuthenticationPrincipal Long userId,
            @RequestParam LocalDate date) {

        CalendarDailyResponse response = calendarService.getDailyRecords(userId, date);

        return ApiResponse.success("해당 날짜 독서 기록 조회 성공", response);
    }

    // ==================== 신규 Schedule API ====================

    @PostMapping("/schedule")
    @Operation(summary = "일정 등록", description = "책장에서 도서에 독서 일정을 등록합니다. 여러 독자(본인/자녀)의 일정을 동시에 등록할 수 있습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일정 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "도서/자녀를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<ApiResponse<ScheduleCreateResponse>> createSchedules(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ScheduleCreateRequest request) {

        ScheduleCreateResponse response = scheduleService.createSchedules(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("일정이 등록되었습니다.", response));
    }

    @GetMapping("/schedule/{year}/{month}")
    @Operation(summary = "월별 일정 조회", description = "월간 독서 일정 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ApiResponse<List<ScheduleResponse>> getMonthlySchedules(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "연도", example = "2025") @PathVariable("year") int year,
            @Parameter(description = "월", example = "1") @PathVariable("month") int month) {

        List<ScheduleResponse> response = scheduleService.getMonthlySchedules(userId, year, month);

        return ApiResponse.success(response);
    }

    @GetMapping("/schedule/date")
    @Operation(summary = "일별 일정 조회", description = "특정 날짜의 독서 일정을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ApiResponse<List<ScheduleResponse>> getDailySchedules(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "조회 날짜", example = "2025-01-15") @RequestParam LocalDate date) {

        List<ScheduleResponse> response = scheduleService.getDailySchedules(userId, date);

        return ApiResponse.success(response);
    }

    @PutMapping("/schedule/{scheduleId}")
    @Operation(summary = "개별 일정 수정", description = "특정 독자의 일정만 수정합니다. 다른 독자의 일정에는 영향 없음.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ApiResponse<ScheduleResponse> updateSchedule(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "일정 ID", example = "1001") @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleUpdateRequest request) {

        ScheduleResponse response = scheduleService.updateSchedule(userId, scheduleId, request);

        return ApiResponse.success("일정이 수정되었습니다.", response);
    }

    @DeleteMapping("/schedule/{scheduleId}")
    @Operation(summary = "개별 일정 삭제", description = "특정 독자의 일정만 삭제합니다. 다른 독자의 일정에는 영향 없음.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ApiResponse<ScheduleDeleteResponse> deleteSchedule(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "일정 ID", example = "1002") @PathVariable Long scheduleId) {

        ScheduleDeleteResponse response = scheduleService.deleteSchedule(userId, scheduleId);

        return ApiResponse.success("일정이 삭제되었습니다.", response);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "도서별 일정 조회", description = "특정 도서에 등록된 모든 독자의 일정을 조회합니다. (책장 모달에서 사용)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ApiResponse<BookSchedulesResponse> getBookSchedules(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "도서 ID", example = "123") @PathVariable Long bookId) {

        BookSchedulesResponse response = scheduleService.getBookSchedules(userId, bookId);

        return ApiResponse.success(response);
    }

    @DeleteMapping("/book/{bookId}")
    @Operation(summary = "도서별 일정 전체 삭제", description = "특정 도서의 모든 일정을 삭제합니다. (도서 삭제 시 연동)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ApiResponse<BookScheduleDeleteResponse> deleteBookSchedules(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "도서 ID", example = "123") @PathVariable Long bookId) {

        BookScheduleDeleteResponse response = scheduleService.deleteBookSchedules(userId, bookId);

        return ApiResponse.success("도서의 모든 일정이 삭제되었습니다.", response);
    }
}