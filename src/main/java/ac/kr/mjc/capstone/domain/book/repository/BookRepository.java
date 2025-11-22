package ac.kr.mjc.capstone.domain.book.repository;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.domain.book.entity.ReadingStatus;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByUserOrderByBookIdDesc(UserEntity userEntity);
    Optional<Book> findByBookIdAndUser(Long bookId, UserEntity userEntity);

    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN FETCH b.bookDetails bd " +
            "LEFT JOIN FETCH b.image i " +
            "WHERE b.user.userId = :userId " +
            "AND (:status IS NULL OR bd.readingStatus = :status) " +
            "AND (:readerId IS NULL OR bd.reader.readerId = :readerId)")
    List<Book> findByReadingStatusAndReader(
            @Param("userId") Long userId,
            @Param("status") Optional<ReadingStatus> status,
            @Param("readerId") Optional<Long> readerId);
}
