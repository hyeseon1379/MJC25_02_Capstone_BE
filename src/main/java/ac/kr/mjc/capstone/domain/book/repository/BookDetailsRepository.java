package ac.kr.mjc.capstone.domain.book.repository;

import ac.kr.mjc.capstone.domain.book.entity.BookDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookDetailsRepository extends JpaRepository<BookDetails, Long> {

    List<BookDetails> findAllByBook_BookId(Long bookId);
}
