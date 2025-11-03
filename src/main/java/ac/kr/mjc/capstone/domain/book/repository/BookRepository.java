package ac.kr.mjc.capstone.domain.book.repository;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
