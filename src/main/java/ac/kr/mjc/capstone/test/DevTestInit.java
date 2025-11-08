package ac.kr.mjc.capstone.test;

import ac.kr.mjc.capstone.domain.book.entity.*;
import ac.kr.mjc.capstone.domain.book.repository.BookDetailsRepository;
import ac.kr.mjc.capstone.domain.book.repository.BookRepository;
import ac.kr.mjc.capstone.domain.book.repository.ReaderRepository;
import ac.kr.mjc.capstone.domain.children.entity.ChildGender;
import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import ac.kr.mjc.capstone.domain.children.repository.ChildrenRepository;
import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Component
@RequiredArgsConstructor
public class DevTestInit implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final ChildrenRepository childrenRepository;
    private final BookDetailsRepository bookDetailsRepository;

    @Override
    public void run(String... args) {

        String runLocal = System.getProperty("runLocal");
        if (!"true".equals(runLocal)) {
            // 로컬이 아니면 실행X, VM option 설정 : -DrunLocal=true
            return;
        }

        String encodedPassword1 = passwordEncoder.encode("password123");

        // --------유저 정보 ---------
        UserEntity userEntity = UserEntity.builder()
                .email("user@example.com")
                .username("user1")
                .password(encodedPassword1)
                .birth(LocalDate.of(2004,6,4))
                .phone("010-1111-1111")
                .nickname("userNick1")
                .color("#FFFFFF")
                .address("address1")
                .profileImg("user1.png")
                .role(Role.USER)
                .build();

        userRepository.save(userEntity);

        UserEntity userEntity2 = UserEntity.builder()
                .email("user2@example.com")
                .username("user2")
                .password(encodedPassword1)
                .birth(LocalDate.of(2005,6,4))
                .phone("010-2222-2222")
                .nickname("userNick2")
                .color("#FFFFFF")
                .address("address2")
                .profileImg("user2.png")
                .role(Role.USER)
                .build();

        userRepository.save(userEntity2);

        // --------자녀 정보 ---------
        ChildrenEntity childrenEntity1 = addchild("child1", LocalDate.of(2021,1,1),
                1, "#000000", "child1.png", ChildGender.F, userEntity);

        ChildrenEntity childrenEntity2 = addchild("child2", LocalDate.of(2022,1,1),
                2, "#000000", "child2.png", ChildGender.M, userEntity);

        ChildrenEntity childrenEntity3 = addchild("child3", LocalDate.of(2023,1,1),
                1, "#000000", "child3.png", ChildGender.M, userEntity2);

        ChildrenEntity childrenEntity4 = addchild("child4", LocalDate.of(2024,1,1),
                2, "#000000", "child4.png", ChildGender.F, userEntity2);

        // --------도서 정보 ---------
        Book book1 = addBook(userEntity, "title1", "author1", "publisher1", "book1.png");
        Book book2 = addBook(userEntity2, "title1", "author1", "publisher1", "book1.png");
        Book book3 = addBook(userEntity, "title2", "author2", "publisher2", "book2.png");
        Book book4 = addBook(userEntity2, "title2", "author2", "publisher2", "book2.png");
        Book book5 = addBook(userEntity2, "title3", "author3", "publisher3", "book3.png");

        Reader reader1 = addReader(userEntity, null, ReaderType.ADULT);
        Reader reader2 = addReader(userEntity, childrenEntity1, ReaderType.CHILD);
        Reader reader3 = addReader(userEntity, childrenEntity2, ReaderType.CHILD);

        Reader reader4 = addReader(userEntity2, null, ReaderType.ADULT);
        Reader reader5 = addReader(userEntity2, childrenEntity3, ReaderType.CHILD);
        Reader reader6 = addReader(userEntity2, childrenEntity4, ReaderType.CHILD);

        BookDetails bookDetails1 = addBookDetails(book1, reader1, ReadingStatus.TO_READ,
                LocalDate.of(2026,1,1), LocalDate.of(2026,1,10));
        BookDetails bookDetails2 = addBookDetails(book1, reader2, ReadingStatus.READING,
                LocalDate.of(2025,11,1), LocalDate.of(2026,1,1));
        BookDetails bookDetails3 = addBookDetails(book3, reader2, ReadingStatus.COMPLETED,
                LocalDate.of(2025,9,1), LocalDate.of(2025,10,1));
        BookDetails bookDetails4 = addBookDetails(book3, reader3, ReadingStatus.COMPLETED,
                LocalDate.of(2024,1,1), LocalDate.of(2024,1,4));

        BookDetails bookDetails5 = addBookDetails(book2, reader4, ReadingStatus.TO_READ,
                LocalDate.of(2026,1,1), LocalDate.of(2026,2,10));
        BookDetails bookDetails6 = addBookDetails(book4, reader5, ReadingStatus.READING,
                LocalDate.of(2025,11,1), LocalDate.of(2025,12,21));
        BookDetails bookDetails7 = addBookDetails(book5, reader6, ReadingStatus.COMPLETED,
                LocalDate.of(2024,1,1), LocalDate.of(2024,1,7));


    }

    public ChildrenEntity addchild(String childName, LocalDate childBirth, Integer birthOrder,
                                   String color, String profileImg, ChildGender gender, UserEntity userEntity){
        ChildrenEntity childrenEntity = ChildrenEntity.builder()
            .childName(childName)
            .childBirth(childBirth)
            .birthOrder(birthOrder)
            .color(color)
            .profileImg(profileImg)
            .gender(gender)
            .userEntity(userEntity)
            .build();

        childrenRepository.save(childrenEntity);
        return childrenEntity;
    }

    public Reader addReader(UserEntity userEntity, ChildrenEntity childrenEntity, ReaderType readerType){
        Reader reader = Reader.builder()
                .userEntity(userEntity)
                .childrenEntity(childrenEntity)
                .readerType(readerType)
                .build();

        readerRepository.save(reader);
        return reader;
    }

    public BookDetails addBookDetails(Book book, Reader reader, ReadingStatus readingStatus,
                                      LocalDate startDate, LocalDate endDate){
        BookDetails bookDetails = BookDetails.builder()
                .book(book)
                .reader(reader)
                .readingStatus(readingStatus)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        book.getBookDetails().add(bookDetails);

        bookDetailsRepository.save(bookDetails);
        return bookDetails;
    }

    public Book addBook(UserEntity userEntity, String title, String author, String publisher, String imgUrl){
        Book book = Book.builder()
                .user(userEntity)
                .title(title)
                .author(author)
                .publisher(publisher)
                .imgUrl(imgUrl)
                .build();

        bookRepository.save(book);
        return book;
    }
}
