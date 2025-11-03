package ac.kr.mjc.capstone.domain.book.controller;

import ac.kr.mjc.capstone.domain.book.dto.BookRequest;
import ac.kr.mjc.capstone.domain.book.dto.BookResponse;
import ac.kr.mjc.capstone.domain.book.service.inf.BookService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

}
