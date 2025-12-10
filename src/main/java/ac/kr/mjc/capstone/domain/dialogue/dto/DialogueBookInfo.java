package ac.kr.mjc.capstone.domain.dialogue.dto;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogueBookInfo {
    private Long bookId;
    private String title;
    private String author;
    private String coverUrl;
    private Long imageId;

    public static DialogueBookInfo from(Book book) {
        if (book == null) {
            return null;
        }
        return DialogueBookInfo.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverUrl(book.getCoverUrl())
                .imageId(book.getImage() != null ? book.getImage().getImageId() : null)
                .build();
    }
}
