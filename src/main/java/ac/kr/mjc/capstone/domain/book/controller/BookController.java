package ac.kr.mjc.capstone.domain.book.controller;

import ac.kr.mjc.capstone.domain.book.dto.*;
import ac.kr.mjc.capstone.domain.book.service.inf.BookService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "도서 관리 API")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "도서 정보 생성", description = "도서 정보를 생성합니다")
    public ApiResponse<BookResponse> createBook(@AuthenticationPrincipal Long userId,
                                                @Valid @RequestBody BookRequest request){
        BookResponse bookResponse = bookService.createBook(userId, request);
        return ApiResponse.success("도서 등록 성공", bookResponse);
    }

    @Operation(summary = "도서 정보 조회", description = "사용자의 도서 정보를 조회합니다")
    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> getMyBook(
            @AuthenticationPrincipal Long userId,
            @PathVariable("bookId") Long bookId) {
        ApiResponse<BookResponse> bookResponse = bookService.getMyBook(userId, bookId);
        return ResponseEntity.status(200).body(bookResponse);
    }

    @Operation(summary = "도서 목록 조회", description = "사용자의 도서 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookListResponse>>> getAllMyBook(@AuthenticationPrincipal Long userId) {
        ApiResponse<List<BookListResponse>> bookListResponse = bookService.getAllMyBook(userId);
        return ResponseEntity.status(200).body(bookListResponse);
    }

    @PutMapping("/{bookId}")
    @Operation(summary = "도서 정보 수정", description = "도서 정보를 수정합니다")
    public ApiResponse<BookResponse> updateBook(@AuthenticationPrincipal Long userId,
                                                @PathVariable("bookId") Long bookId,
                                                @Valid @RequestBody BookUpdateRequest request){

        BookResponse bookResponse = bookService.updateBook(userId, bookId, request);
        return ApiResponse.success("도서 정보 수정 성공", bookResponse);

    }

    @Operation(summary = "도서 삭제", description = "도서를 삭제합니다")
    @DeleteMapping("/{bookId}")
    public ApiResponse<Void> deleteBook(@AuthenticationPrincipal Long userId,
                                        @PathVariable("bookId") Long bookId) {

        bookService.deleteBook(userId, bookId);

        return ApiResponse.success("도서 삭제 성공");
    }

    @Operation(summary = "도서 리스트 삭제", description = "도서 리스트를 삭제합니다")
    @DeleteMapping
    public ApiResponse<Void> deleteBooks(@AuthenticationPrincipal Long userId,
                                         @RequestBody BookDeleteRequest bookDeleteRequest) {

        bookService.deleteBooks(userId, bookDeleteRequest);

        return ApiResponse.success("도서 목록 삭제 성공");
    }

}
