package ac.kr.mjc.capstone.domain.book.entity;

import ac.kr.mjc.capstone.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id")
    private Long detailsId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public void update(Reader reader, ReadingStatus readingStatus, LocalDate startDate, LocalDate endDate) {
        this.reader = reader;
        this.readingStatus = readingStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
