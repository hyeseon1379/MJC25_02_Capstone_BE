package ac.kr.mjc.capstone.domain.calendar.service.impl;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.domain.book.repository.BookRepository;
import ac.kr.mjc.capstone.domain.calendar.dto.*;
import ac.kr.mjc.capstone.domain.calendar.entity.CalendarSchedule;
import ac.kr.mjc.capstone.domain.calendar.repository.CalendarScheduleRepository;
import ac.kr.mjc.capstone.domain.calendar.service.inf.CalendarScheduleService;
import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import ac.kr.mjc.capstone.domain.children.repository.ChildrenRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarScheduleServiceImpl implements CalendarScheduleService {

    private final CalendarScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ChildrenRepository childrenRepository;

    @Override
    public ScheduleCreateResponse createSchedules(Long userId, ScheduleCreateRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "도서를 찾을 수 없습니다."));

        // 도서 소유자 확인
        if (!book.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "해당 도서에 대한 권한이 없습니다.");
        }

        List<CalendarSchedule> savedSchedules = new ArrayList<>();

        for (ScheduleCreateRequest.ScheduleItem item : request.getSchedules()) {
            ChildrenEntity child = null;
            if (item.getChildId() != null) {
                child = childrenRepository.findById(item.getChildId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "자녀 정보를 찾을 수 없습니다."));

                // 자녀 소유자 확인
                if (!child.getUserEntity().getUserId().equals(userId)) {
                    throw new CustomException(ErrorCode.FORBIDDEN, "해당 자녀에 대한 권한이 없습니다.");
                }
            }

            CalendarSchedule schedule = CalendarSchedule.builder()
                    .user(user)
                    .book(book)
                    .child(child)
                    .startDate(item.getStartDate())
                    .endDate(item.getEndDate())
                    .build();

            schedule.setStatus(schedule.calculateStatus());
            savedSchedules.add(scheduleRepository.save(schedule));
        }

        List<ScheduleCreateResponse.ScheduleItem> items = savedSchedules.stream()
                .map(ScheduleCreateResponse.ScheduleItem::from)
                .collect(Collectors.toList());

        return ScheduleCreateResponse.builder()
                .bookId(request.getBookId())
                .schedules(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getMonthlySchedules(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<CalendarSchedule> schedules = scheduleRepository.findAllByUserAndMonth(userId, startDate, endDate);

        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getDailySchedules(Long userId, LocalDate date) {
        List<CalendarSchedule> schedules = scheduleRepository.findAllByUserAndDate(userId, date);

        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request) {
        CalendarSchedule schedule = scheduleRepository.findByIdAndUser(scheduleId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "일정을 찾을 수 없습니다."));

        ChildrenEntity child = null;
        if (request.getChildId() != null) {
            child = childrenRepository.findById(request.getChildId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "자녀 정보를 찾을 수 없습니다."));

            // 자녀 소유자 확인
            if (!child.getUserEntity().getUserId().equals(userId)) {
                throw new CustomException(ErrorCode.FORBIDDEN, "해당 자녀에 대한 권한이 없습니다.");
            }
        }

        schedule.updateSchedule(child, request.getStartDate(), request.getEndDate());

        return ScheduleResponse.from(schedule);
    }

    @Override
    public ScheduleDeleteResponse deleteSchedule(Long userId, Long scheduleId) {
        CalendarSchedule schedule = scheduleRepository.findByIdAndUser(scheduleId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "일정을 찾을 수 없습니다."));

        scheduleRepository.delete(schedule);

        return ScheduleDeleteResponse.of(scheduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookSchedulesResponse getBookSchedules(Long userId, Long bookId) {
        // 도서 존재 및 권한 확인
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "도서를 찾을 수 없습니다."));

        if (!book.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "해당 도서에 대한 권한이 없습니다.");
        }

        List<CalendarSchedule> schedules = scheduleRepository.findAllByUserAndBook(userId, bookId);

        if (schedules.isEmpty()) {
            return BookSchedulesResponse.builder()
                    .book(ScheduleBookInfo.from(book))
                    .schedules(new ArrayList<>())
                    .build();
        }

        return BookSchedulesResponse.from(schedules);
    }

    @Override
    public BookScheduleDeleteResponse deleteBookSchedules(Long userId, Long bookId) {
        // 도서 존재 및 권한 확인
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "도서를 찾을 수 없습니다."));

        if (!book.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "해당 도서에 대한 권한이 없습니다.");
        }

        long count = scheduleRepository.countByBookBookIdAndUserUserId(bookId, userId);
        scheduleRepository.deleteAllByBookBookIdAndUserUserId(bookId, userId);

        return BookScheduleDeleteResponse.of(bookId, count);
    }
}
