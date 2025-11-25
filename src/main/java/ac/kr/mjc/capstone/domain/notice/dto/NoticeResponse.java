package ac.kr.mjc.capstone.domain.notice.dto;

import ac.kr.mjc.capstone.domain.notice.entity.NoticeEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {

    private Long noticeId;
    private String title;
    private String content;
    private Long userId;
    private String username;
    private ImageFileEntity imageFile; // BoardImageResponse로 변경
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static NoticeResponse from(NoticeEntity notice) {
        return NoticeResponse.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .userId(notice.getUser().getUserId())
                .username(notice.getUser().getUsername())
                .imageFile(notice.getFileEntity())
                .createAt(notice.getCreateAt())
                .updateAt(notice.getUpdateAt())
                .build();
    }
}
