package ac.kr.mjc.capstone.domain.board.dto;

import ac.kr.mjc.capstone.domain.board.entity.BoardEntity;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BoardResponse {
    private Long boardId;

    private String title;

    private String content;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private UserEntity user;

    private ImageFileEntity imageFile;

    public static BoardResponse from(BoardEntity entity){
        return BoardResponse.builder()
                .boardId(entity.getBoardId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .user(entity.getUser())
                .imageFile(entity.getImageFile())
                .build();
    }
}
