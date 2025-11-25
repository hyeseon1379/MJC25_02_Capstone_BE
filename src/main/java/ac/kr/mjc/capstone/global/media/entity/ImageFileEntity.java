package ac.kr.mjc.capstone.global.media.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ImageFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", length = 255)
    private String filePath;

    @Column(name = "usage_type", length = 50)
    @Enumerated(EnumType.STRING) // ENUM타입을 DB에 String타입으로 저장
    private ImageUsageType usageType;
}
