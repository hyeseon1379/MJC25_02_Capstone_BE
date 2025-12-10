package ac.kr.mjc.capstone.domain.share.entity;

import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.global.base.BaseEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "share_board", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_meet_status", columnList = "meet_status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShareBoardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id")
    private Long shareId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private ImageFileEntity imageFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "meet_status", nullable = false)
    @Builder.Default
    private MeetStatus meetStatus = MeetStatus.SHARING;

    @Column(name = "views", nullable = false)
    @Builder.Default
    private Integer views = 0;

    // 게시글 수정
    public void updateShareBoard(String title, String content, MeetStatus meetStatus, ImageFileEntity imageFile) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (meetStatus != null) {
            this.meetStatus = meetStatus;
        }
        if (imageFile != null) {
            this.imageFile = imageFile;
        }
    }

    // 이미지 제거
    public void removeImage() {
        this.imageFile = null;
    }

    // 상태 변경
    public void updateStatus(MeetStatus meetStatus) {
        this.meetStatus = meetStatus;
    }

    // 조회수 증가
    public void incrementViews() {
        this.views++;
    }
}
