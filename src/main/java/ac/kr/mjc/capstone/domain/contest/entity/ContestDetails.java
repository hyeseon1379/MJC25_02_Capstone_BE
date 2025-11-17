package ac.kr.mjc.capstone.domain.contest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contest_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id")
    private Long detailsId;

    @Column(name = "start_prompt")
    private String startPrompt;

    @Enumerated(EnumType.STRING)
    private Round round;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ProgressStatus progressStatus;

}
