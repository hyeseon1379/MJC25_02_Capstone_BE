package ac.kr.mjc.capstone.domain.book.service.impl;

import ac.kr.mjc.capstone.domain.book.dto.*;
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
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.media.repository.FileRepository;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
    private final FileRepository fileRepository;

    @Override
    public BookResponse createBook(Long userId, BookRequest bookRequest){

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ImageFileEntity image = fileRepository.findById(bookRequest.getImageId())
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .publisher(bookRequest.getPublisher())
                .image(image)
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

    @Override
    public BookResponse updateBook(Long userId, Long bookId, BookUpdateRequest bookRequest){

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!book.getUser().equals(userEntity)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        ImageFileEntity newImage = null;
        if (bookRequest.getImageId() != null) {
            newImage = fileRepository.findById(bookRequest.getImageId())
                    .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        }

        book.update(
                bookRequest.getTitle() != null ? bookRequest.getTitle() : book.getTitle(),
                bookRequest.getAuthor() != null ? bookRequest.getAuthor() : book.getAuthor(),
                bookRequest.getPublisher() != null ? bookRequest.getPublisher() : book.getPublisher(),
                newImage != null ? newImage : book.getImage()
        );

        List<BookDetailsUpdateRequest> requestedDetails = bookRequest.getBookDetailsUpdate();

        // 요청 DTO에 상세 정보가 없으면, 기존 상세 정보 모두 삭제
        if (requestedDetails == null || requestedDetails.isEmpty()) {
            bookDetailsRepository.deleteAll(book.getBookDetails());
            book.getBookDetails().clear();
        } else {
            updateBookDetailsList(userEntity, book, requestedDetails);
        }

        return BookResponse.from(book);
    }

    @Override
    public void deleteBook(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        if (!book.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        bookRepository.delete(book);
    }

    @Override
    public void deleteBooks(Long userId,  BookDeleteRequest bookDeleteRequest) {

        List<Long> bookIds = bookDeleteRequest.getBookIds();

        if (bookIds == null || bookIds.isEmpty()) {
            throw new CustomException(ErrorCode.BOOK_NOT_FOUND);
        }

        List<Book> books = bookRepository.findAllById(bookIds);

        if (books.size() != bookIds.size()) {
            throw new CustomException(ErrorCode.BOOK_NOT_FOUND, "요청된 도서 중 존재하지 않는 ID가 포함되어 있습니다.");
        }

        boolean allOwned = books.stream()
                .allMatch(book -> book.getUser().getUserId().equals(userId));

        if (!allOwned) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "삭제 권한이 없는 도서가 목록에 포함되어 있습니다.");
        }

       bookRepository.deleteAll(books);;
    }

    @Override
    public List<BookResponse> searchBooksByCategory(Long userId,BookSearchRequest request) {

        Optional<ReadingStatus> status = Optional.ofNullable(request.getReadingStatus());
        Optional<Long> readerId = Optional.ofNullable(request.getReaderId());

        List<Book> books = bookRepository.findByReadingStatusAndReader(userId, status, readerId);

        return books.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    private Reader createReader(UserEntity userEntity, BookDetailsRequest detailRequest) {
        if(detailRequest.getChildId() != null) {
            ChildrenEntity childrenEntity = childrenRepository.findById(detailRequest.getChildId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

            if (!childrenEntity.getUserEntity().equals(userEntity)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

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

    private Reader UpdateReader(UserEntity userEntity, BookDetailsUpdateRequest request) {

        Reader reader = null;

        if (request.getReaderId() != null) {
            reader = readerRepository.findById(request.getReaderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.READER_NOT_FOUND));

            if (!reader.getUserEntity().equals(userEntity)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_READER_OWNERSHIP);
            }

            if(reader.getReaderType().equals(ReaderType.ADULT)){

                if(request.getChildId() != null){
                    throw new CustomException(ErrorCode.READER_CHILD_MISMATCH);
                }

                return reader;

            } else {
                if(!request.getChildId().equals(reader.getChildrenEntity().getChildId())){
                    throw new CustomException(ErrorCode.CHILD_ID_MISMATCH);
                }
                return reader;
            }
        } else {
            if(request.getChildId() != null) {
                ChildrenEntity childrenEntity = childrenRepository.findById(request.getChildId())
                        .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

                readerRepository.findByChildrenEntity_ChildId(request.getChildId())
                        .ifPresent(reader1 -> {
                            throw new CustomException(ErrorCode.READER_ALREADY_EXISTS);
                        });

                if (!childrenEntity.getUserEntity().equals(userEntity)) {
                    throw new CustomException(ErrorCode.UNAUTHORIZED);
                }

                reader = Reader.builder()
                        .userEntity(userEntity)
                        .childrenEntity(childrenEntity)
                        .readerType(ReaderType.CHILD)
                        .build();

                readerRepository.save(reader);
            }else{
                throw new CustomException(ErrorCode.CHILD_NOT_FOUND);
            }
            return reader;
        }
    }

    private void updateBookDetailsList(UserEntity userEntity, Book book, List<BookDetailsUpdateRequest> requestedDetails) {

        Map<Long, BookDetails> existingDetailsMap = book.getBookDetails().stream()
                .collect(Collectors.toMap(BookDetails::getDetailsId, Function.identity()));

        List<BookDetails> newDetails = new ArrayList<>();

        for (BookDetailsUpdateRequest request : requestedDetails) {

            Reader updateReader = UpdateReader(userEntity, request);

            if (request.getDetailsId() != null && existingDetailsMap.containsKey(request.getDetailsId())) {
                // 수정
                BookDetails detailToUpdate = existingDetailsMap.get(request.getDetailsId());

                detailToUpdate.update(
                        updateReader,
                        calculateReadingStatus(request.getStartDate(),request.getEndDate()),
                        request.getStartDate(),
                        request.getEndDate()
                );

                existingDetailsMap.remove(request.getDetailsId());
                newDetails.add(detailToUpdate);

            } else if (request.getDetailsId() == null) {
                // 추가
                BookDetails detailToCreate = BookDetails.builder()
                        .book(book)
                        .reader(updateReader)
                        .readingStatus(calculateReadingStatus(request.getStartDate(),request.getEndDate()))
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .build();

                book.getBookDetails().add(detailToCreate);
                newDetails.add(detailToCreate);
            }
        }

        // 삭제
        existingDetailsMap.values().forEach(detailToDelete -> {
            bookDetailsRepository.delete(detailToDelete);
            book.getBookDetails().remove(detailToDelete);
        });
    }
}
