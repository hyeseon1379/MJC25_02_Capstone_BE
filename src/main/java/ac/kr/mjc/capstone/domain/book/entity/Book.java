package ac.kr.mjc.capstone.domain.book.entity;

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

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "author")
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @OneToMany(mappedBy = "Book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookDetails> bookDetails = new ArrayList<>();

}
