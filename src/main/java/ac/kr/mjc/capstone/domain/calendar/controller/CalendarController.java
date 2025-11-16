package ac.kr.mjc.capstone.domain.calendar.controller;

import ac.kr.mjc.capstone.domain.calendar.dto.CalendarDailyResponse;
import ac.kr.mjc.capstone.domain.calendar.dto.CalendarResponse;
import ac.kr.mjc.capstone.domain.calendar.service.inf.CalendarService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{year}/{month}")
    @Operation(summary = "캘린더 조회", description = "월간 캘린더 정보를 조회합니다.")
    public ApiResponse<List<CalendarResponse>> getMonthlyCalendar(
            @AuthenticationPrincipal Long userId,
            @PathVariable("year") int year,
            @PathVariable("month") int month) {

        List<CalendarResponse> response = calendarService.getMonthlyCalendar(userId, year, month);

        return ApiResponse.success("월간 독서 캘린더 조회 성공", response);
    }


    @GetMapping("/date")
    @Operation(summary = "캘린더 상세 조회", description = "특정 날짜의 도서 상세 목록을 조회합니다.")
    public ApiResponse<CalendarDailyResponse> getDailyRecords(
            @AuthenticationPrincipal Long userId,
            @RequestParam LocalDate date) {

        CalendarDailyResponse response = calendarService.getDailyRecords(userId, date);

        return ApiResponse.success("해당 날짜 독서 기록 조회 성공", response);
    }
}