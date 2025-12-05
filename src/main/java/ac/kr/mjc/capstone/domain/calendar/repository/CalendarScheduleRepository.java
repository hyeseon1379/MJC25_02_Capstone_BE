package ac.kr.mjc.capstone.domain.calendar.repository;

import ac.kr.mjc.capstone.domain.calendar.entity.CalendarSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule, Long> {

    /**
     * 사용자의 월별 일정 조회
     */
    @Query("SELECT cs FROM CalendarSchedule cs " +
           "LEFT JOIN FETCH cs.book b " +
           "LEFT JOIN FETCH cs.child c " +
           "LEFT JOIN FETCH cs.user u " +
           "WHERE cs.user.userId = :userId " +
           "AND ((cs.startDate BETWEEN :startDate AND :endDate) " +
           "OR (cs.endDate BETWEEN :startDate AND :endDate) " +
           "OR (cs.startDate <= :startDate AND (cs.endDate IS NULL OR cs.endDate >= :endDate)))")
    List<CalendarSchedule> findAllByUserAndMonth(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 사용자의 특정 날짜 일정 조회
     */
    @Query("SELECT cs FROM CalendarSchedule cs " +
           "LEFT JOIN FETCH cs.book b " +
           "LEFT JOIN FETCH cs.child c " +
           "LEFT JOIN FETCH cs.user u " +
           "WHERE cs.user.userId = :userId " +
           "AND cs.startDate <= :date " +
           "AND (cs.endDate IS NULL OR cs.endDate >= :date)")
    List<CalendarSchedule> findAllByUserAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date);

    /**
     * 사용자의 특정 도서 일정 조회
     */
    @Query("SELECT cs FROM CalendarSchedule cs " +
           "LEFT JOIN FETCH cs.book b " +
           "LEFT JOIN FETCH cs.child c " +
           "LEFT JOIN FETCH cs.user u " +
           "WHERE cs.user.userId = :userId " +
           "AND cs.book.bookId = :bookId")
    List<CalendarSchedule> findAllByUserAndBook(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId);

    /**
     * 일정 상세 조회 (사용자 권한 확인 포함)
     */
    @Query("SELECT cs FROM CalendarSchedule cs " +
           "LEFT JOIN FETCH cs.book b " +
           "LEFT JOIN FETCH cs.child c " +
           "LEFT JOIN FETCH cs.user u " +
           "WHERE cs.scheduleId = :scheduleId " +
           "AND cs.user.userId = :userId")
    Optional<CalendarSchedule> findByIdAndUser(
            @Param("scheduleId") Long scheduleId,
            @Param("userId") Long userId);

    /**
     * 도서의 모든 일정 삭제
     */
    void deleteAllByBookBookIdAndUserUserId(Long bookId, Long userId);

    /**
     * 도서의 일정 개수 조회
     */
    long countByBookBookIdAndUserUserId(Long bookId, Long userId);
}
