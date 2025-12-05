package ac.kr.mjc.capstone.domain.calendar.service.inf;

import ac.kr.mjc.capstone.domain.calendar.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface CalendarScheduleService {

    /**
     * 일정 등록
     */
    ScheduleCreateResponse createSchedules(Long userId, ScheduleCreateRequest request);

    /**
     * 월별 일정 조회
     */
    List<ScheduleResponse> getMonthlySchedules(Long userId, int year, int month);

    /**
     * 일별 일정 조회
     */
    List<ScheduleResponse> getDailySchedules(Long userId, LocalDate date);

    /**
     * 개별 일정 수정
     */
    ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request);

    /**
     * 개별 일정 삭제
     */
    ScheduleDeleteResponse deleteSchedule(Long userId, Long scheduleId);

    /**
     * 도서별 일정 조회
     */
    BookSchedulesResponse getBookSchedules(Long userId, Long bookId);

    /**
     * 도서의 모든 일정 삭제
     */
    BookScheduleDeleteResponse deleteBookSchedules(Long userId, Long bookId);
}
