package ac.kr.mjc.capstone.domain.book.service.impl;

import ac.kr.mjc.capstone.domain.book.dto.BookDetailsRequest;
import ac.kr.mjc.capstone.domain.book.dto.BookListResponse;
import ac.kr.mjc.capstone.domain.book.dto.BookRequest;
import ac.kr.mjc.capstone.domain.book.dto.BookResponse;
import ac.kr.mjc.capstone.domain.book.entity.*;
import ac.kr.mjc.capstone.domain.book.repository.BookDetailsRepository;
import ac.kr.mjc.capstone.domain.book.repository.BookRepository;
import ac.kr.mjc.capstone.domain.book.repository.ReaderRepository;
import ac.kr.mjc.capstone.domain.book.service.inf.BookService;
import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import ac.kr.mjc.capstone.domain.children.repository.ChildrenRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final ChildrenRepository childrenRepository;
    private final BookDetailsRepository bookDetailsRepository;

    @Override
    public BookResponse createBook(Long userId, BookRequest bookRequest){

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .publisher(bookRequest.getPublisher())
                .imgUrl(bookRequest.getImgUrl())
                .user(userEntity)
                .build();

        if(bookRequest.getBookDetails() == null || bookRequest.getBookDetails().isEmpty()) {
            Book saveBook = bookRepository.save(book);
            return BookResponse.from(saveBook);
        }

        List<BookDetails> detailsToSave = bookRequest.getBookDetails().stream()
                .map(detailRequest -> {

                    Reader newReader = createReader(userEntity, detailRequest);

                    BookDetails bookDetails = BookDetails.builder()
                            .reader(newReader)
                            .readingStatus(calculateReadingStatus(detailRequest.getStartDate()
                                    ,detailRequest.getEndDate()))
                            .startDate(detailRequest.getStartDate())
                            .endDate(detailRequest.getEndDate())
                            .book(book)
                            .build();

                    return bookDetails;
                })
                .collect(Collectors.toList());

        book.setBookDetails(detailsToSave);

        Book savedBook = bookRepository.save(book);

        return BookResponse.from(savedBook);

    }

    @Override
    public ApiResponse<BookResponse> getMyBook(Long userId,Long bookId){

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findByBookIdAndUser(bookId, userEntity)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        BookResponse bookResponse = BookResponse.from(book);

        return ApiResponse.success("도서 정보 조회 성공", bookResponse);
    }

    @Override
    public ApiResponse<List<BookListResponse>> getAllMyBook(Long userId){

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<BookListResponse> bookListResponse = bookRepository.findByUserOrderByBookIdDesc(userEntity).stream()
                .map(BookListResponse::from)
                .toList();

        return ApiResponse.success("사용자 도서 목록 조회 성공", bookListResponse);
    }

    private Reader createReader(UserEntity userEntity, BookDetailsRequest detailRequest) {
        if(detailRequest.getChildId() != null) {
            ChildrenEntity childrenEntity = childrenRepository.findById(detailRequest.getChildId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

            Reader reader = Reader.builder()
                    .userEntity(userEntity)
                    .childrenEntity(childrenEntity)
                    .readerType(ReaderType.CHILD)
                    .build();

            return readerRepository.save(reader);
        }

        Reader reader = Reader.builder()
                .userEntity(userEntity)
                .readerType(ReaderType.ADULT)
                .build();

        return readerRepository.save(reader);
    }

    private ReadingStatus calculateReadingStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return ReadingStatus.TO_READ;
        } else if (!today.isAfter(endDate)) {
            return ReadingStatus.READING;
        } else {
            return ReadingStatus.COMPLETED;
        }
    }

}
