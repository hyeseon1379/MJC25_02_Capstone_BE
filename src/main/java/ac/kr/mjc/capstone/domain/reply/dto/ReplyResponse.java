package ac.kr.mjc.capstone.domain.reply.dto;

import ac.kr.mjc.capstone.domain.reply.entity.ReplyEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "댓글 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyResponse {
    
    @Schema(description = "댓글 ID", example = "1")
    private Long replyId;
    
    @Schema(description = "게시글 ID", example = "1")
    private Long boardId;
    
    @Schema(description = "작성자 ID", example = "1")
    private Long userId;
    
    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String userNickname;
    
    @Schema(description = "작성자 프로필 이미지", example = "https://example.com/profile.jpg")
    private String userProfileImg;
    
    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    private String content;
    
    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    
    @Schema(description = "수정일시", example = "2024-01-15T11:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
    
    // Entity -> Response DTO 변환
    public static ReplyResponse from(ReplyEntity entity) {
        return ReplyResponse.builder()
                .replyId(entity.getReplyId())
                .boardId(entity.getBoard().getBoardId())
                .userId(entity.getUser().getUserId())
                .userNickname(entity.getUser().getNickname())
                .userProfileImg(entity.getUser().getProfileImg())
                .content(entity.getContent())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }
}
