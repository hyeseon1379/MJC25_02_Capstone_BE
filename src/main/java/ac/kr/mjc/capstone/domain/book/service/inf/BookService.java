package ac.kr.mjc.capstone.domain.book.service.inf;

import ac.kr.mjc.capstone.domain.book.dto.BookRequest;
import ac.kr.mjc.capstone.domain.book.dto.BookResponse;

import java.util.List;

public interface BookService {
    BookResponse createBook(Long userId, BookRequest bookRequest);
}
