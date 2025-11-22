package ac.kr.mjc.capstone.domain.calendar.service.impl;

import ac.kr.mjc.capstone.domain.book.entity.BookDetails;
import ac.kr.mjc.capstone.domain.book.entity.Reader;
import ac.kr.mjc.capstone.domain.book.repository.BookDetailsRepository;
import ac.kr.mjc.capstone.domain.calendar.dto.*;
import ac.kr.mjc.capstone.domain.calendar.service.inf.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarServiceImpl implements CalendarService {
    private final BookDetailsRepository bookDetailsRepository;

    @Override
    public List<CalendarResponse> getMonthlyCalendar(Long userId, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<BookDetails> detailsList = bookDetailsRepository.findAllByMonth(userId, start, end);

        Map<Integer, List<ReaderInfo>> dayMap = new HashMap<>();

        for (BookDetails details : detailsList) {

            LocalDate loop = details.getStartDate();
            while (!loop.isAfter(details.getEndDate())) {

                if (loop.getMonthValue() == month) {
                    int day = loop.getDayOfMonth();

                    dayMap.putIfAbsent(day, new ArrayList<>());

                    Reader reader = details.getReader();

                    dayMap.get(day).add(
                            ReaderInfo.from(reader)
                    );
                }

                loop = loop.plusDays(1);
            }
        }

        return dayMap.entrySet().stream()
                .map(e -> CalendarResponse.builder()
                        .day(e.getKey())
                        .readers(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CalendarDailyResponse getDailyRecords(Long userId, LocalDate date) {

        List<BookDetails> list = bookDetailsRepository
                .findAllByReader_UserEntity_UserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        userId, date, date);

        List<DailyRecordInfo> records = list.stream()
                .map(bd -> DailyRecordInfo.builder()
                        .detailsId(bd.getDetailsId())
                        .reader(
                                ReaderInfo.from(bd.getReader())
                        )
                        .book(
                                BookInfo.from(bd.getBook())
                        )
                        .startDate(bd.getStartDate())
                        .endDate(bd.getEndDate())
                        .build()
                )
                .collect(Collectors.toList());

        return CalendarDailyResponse.builder()
                .date(date)
                .records(records)
                .build();
    }
}
