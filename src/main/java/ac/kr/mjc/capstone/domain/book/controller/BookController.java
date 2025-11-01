package ac.kr.mjc.capstone.domain.book.controller;

import ac.kr.mjc.capstone.domain.book.dto.BookRequest;
import ac.kr.mjc.capstone.domain.book.dto.BookResponse;
import ac.kr.mjc.capstone.domain.book.service.inf.BookService;
import ac.kr.mjc.capstone.domain.children.dto.ChildrenResponse;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ApiResponse<BookResponse> createBook(@AuthenticationPrincipal Long userId,
                                                @Valid @RequestBody BookRequest request){
        BookResponse bookResponse = bookService.createBook(userId, request);
        return ApiResponse.success("도서 등록 성공", bookResponse);
    }

}
