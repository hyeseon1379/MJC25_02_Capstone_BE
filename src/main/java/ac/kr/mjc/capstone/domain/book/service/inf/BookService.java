package ac.kr.mjc.capstone.domain.book.service.inf;

import ac.kr.mjc.capstone.domain.book.dto.BookListResponse;
import ac.kr.mjc.capstone.domain.book.dto.BookRequest;
import ac.kr.mjc.capstone.domain.book.dto.BookResponse;
import ac.kr.mjc.capstone.domain.book.dto.BookUpdateRequest;
import ac.kr.mjc.capstone.global.response.ApiResponse;

import java.util.List;

public interface BookService {
    BookResponse createBook(Long userId, BookRequest bookRequest);
    ApiResponse<BookResponse> getMyBook(Long userId, Long bookId);
    ApiResponse<List<BookListResponse>> getAllMyBook(Long userId);
    BookResponse updateBook(Long userId, Long bookId, BookUpdateRequest bookRequest);
}
