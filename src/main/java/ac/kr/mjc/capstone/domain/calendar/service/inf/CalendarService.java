package ac.kr.mjc.capstone.domain.calendar.service.inf;

import ac.kr.mjc.capstone.domain.calendar.dto.CalendarDailyResponse;
import ac.kr.mjc.capstone.domain.calendar.dto.CalendarResponse;

import java.time.LocalDate;
import java.util.List;

public interface CalendarService {
    List<CalendarResponse> getMonthlyCalendar(Long userId, int year, int month);
    CalendarDailyResponse getDailyRecords(Long userId, LocalDate date);
}
