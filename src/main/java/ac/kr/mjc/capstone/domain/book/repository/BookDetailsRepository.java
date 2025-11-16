package ac.kr.mjc.capstone.domain.book.repository;

import ac.kr.mjc.capstone.domain.book.entity.BookDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookDetailsRepository extends JpaRepository<BookDetails, Long> {

    List<BookDetails> findAllByBook_BookId(Long bookId);

    @Query("SELECT bd FROM BookDetails bd " +
            "WHERE bd.reader.userEntity.userId = :userId " +
            "AND (bd.startDate <= :endOfMonth AND bd.endDate >= :startOfMonth)")
    List<BookDetails> findAllByMonth(
            @Param("userId") Long userId,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );

    List<BookDetails> findAllByReader_UserEntity_UserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long userId, LocalDate date1, LocalDate date2
    );
}
