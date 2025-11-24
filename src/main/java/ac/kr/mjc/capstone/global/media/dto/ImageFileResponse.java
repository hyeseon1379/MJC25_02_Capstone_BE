package ac.kr.mjc.capstone.global.media.dto;

import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageUsageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageFileResponse {
    private Long imageId;
    private String fileName;
    private String filePath;
    private ImageUsageType usageType;

    public static ImageFileResponse from(ImageFileEntity entity) {
        if (entity == null) {
            return null;
        }

        return ImageFileResponse.builder()
                .imageId(entity.getImageId())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .usageType(entity.getUsageType())
                .build();
    }
}
