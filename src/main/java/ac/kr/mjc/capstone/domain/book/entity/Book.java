package ac.kr.mjc.capstone.domain.book.entity;

import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Book")
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageFileEntity image;

    @Column(name = "author")
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "isbn13")
    private String isbn;

    @Column(name = "publication_year")
    private String publicationYear;


    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookDetails> bookDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    public void update(String title, String author, String publisher, ImageFileEntity image, String isbn, String publicationYear, String coverUrl, String description) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.image = image;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.coverUrl = coverUrl;
        this.description = description;
    }

}
