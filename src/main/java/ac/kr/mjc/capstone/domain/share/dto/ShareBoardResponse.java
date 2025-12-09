package ac.kr.mjc.capstone.domain.share.dto;

import ac.kr.mjc.capstone.domain.share.entity.MeetStatus;
import ac.kr.mjc.capstone.domain.share.entity.ShareBoardEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareBoardResponse {

    private Long shareId;
    private String title;
    private String content;
    private MeetStatus meetStatus;
    private String imageUrl;
    private AuthorInfo author;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer views;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long userId;
        private String nickname;
        private String profileImg;
    }

    /**
     * 상세 조회용 (모든 필드 포함)
     */
    public static ShareBoardResponse fromDetail(ShareBoardEntity entity) {
        return ShareBoardResponse.builder()
                .shareId(entity.getShareId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .meetStatus(entity.getMeetStatus())
                .imageUrl(entity.getImageFile() != null ? entity.getImageFile().getFilePath() : null)
                .author(AuthorInfo.builder()
                        .userId(entity.getUser().getUserId())
                        .nickname(entity.getUser().getNickname())
                        .profileImg(entity.getUser().getProfileImg())
                        .build())
                .createdAt(entity.getCreateAt())
                .updatedAt(entity.getUpdateAt())
                .views(entity.getViews())
                .build();
    }

    /**
     * 목록 조회용 (content 제외, profileImg 제외)
     */
    public static ShareBoardResponse fromList(ShareBoardEntity entity) {
        return ShareBoardResponse.builder()
                .shareId(entity.getShareId())
                .title(entity.getTitle())
                .meetStatus(entity.getMeetStatus())
                .imageUrl(entity.getImageFile() != null ? entity.getImageFile().getFilePath() : null)
                .author(AuthorInfo.builder()
                        .userId(entity.getUser().getUserId())
                        .nickname(entity.getUser().getNickname())
                        .build())
                .createdAt(entity.getCreateAt())
                .views(entity.getViews())
                .build();
    }
}
