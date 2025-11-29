package ac.kr.mjc.capstone.domain.contest.entity;

import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_id")
    private Long contestId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ProgressStatus progressStatus;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageFileEntity image;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder.Default
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestDetails> contestDetails = new ArrayList<>();

    public void update(String title, String content, ProgressStatus progressStatus,
                       LocalDate startDate, LocalDate endDate, ImageFileEntity image) {
        this.title = title;
        this.content = content;
        this.progressStatus = progressStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
    }
}
