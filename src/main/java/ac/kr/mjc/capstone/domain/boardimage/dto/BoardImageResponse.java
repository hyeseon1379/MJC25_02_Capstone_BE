package ac.kr.mjc.capstone.domain.boardimage.dto;

import ac.kr.mjc.capstone.domain.boardimage.entity.BoardImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardImageResponse {
    private Long imageId;
    private String fileName;
    private String filePath;

    public static BoardImageResponse from(BoardImageEntity entity) {
        return BoardImageResponse.builder()
                .imageId(entity.getImageId())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .build();
    }
}
