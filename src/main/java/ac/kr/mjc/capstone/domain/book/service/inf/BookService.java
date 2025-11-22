package ac.kr.mjc.capstone.domain.book.service.inf;

import ac.kr.mjc.capstone.domain.book.dto.*;
import ac.kr.mjc.capstone.global.response.ApiResponse;

import java.util.List;

public interface BookService {
    BookResponse createBook(Long userId, BookRequest bookRequest);
    ApiResponse<BookResponse> getMyBook(Long userId, Long bookId);
    ApiResponse<List<BookListResponse>> getAllMyBook(Long userId);
    BookResponse updateBook(Long userId, Long bookId, BookUpdateRequest bookRequest);
    void deleteBook(Long userId, Long bookId);
    void deleteBooks(Long userId,  BookDeleteRequest bookDeleteRequest);
    List<BookResponse> searchBooksByCategory(Long userId,BookSearchRequest request);
}
