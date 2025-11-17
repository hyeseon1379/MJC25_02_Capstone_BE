package ac.kr.mjc.capstone.domain.contest.entity;

import ac.kr.mjc.capstone.global.base.BaseEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contest_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Column(name = "title")
    private String title;

    @Column(name = "final_content")
    private String finalContent;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageFileEntity image;

    private void update(Contest contest, ImageFileEntity image, String title, String finalContent) {
        this.contest = contest;
        this.image = image;
        this.title = title;
        this.finalContent = finalContent;
    }
}
