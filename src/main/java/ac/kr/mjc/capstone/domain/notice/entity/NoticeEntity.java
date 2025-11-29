package ac.kr.mjc.capstone.domain.notice.entity;

import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.global.base.BaseEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notice", indexes = @Index(name = "idx_notice_title", columnList = "title"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageFileEntity fileEntity;

    // 공지사항 수정 메서드
    public void updateNotice(String title, String content, ImageFileEntity fileEntity) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (fileEntity != null) {
            this.fileEntity = fileEntity;
        }
    }
}
