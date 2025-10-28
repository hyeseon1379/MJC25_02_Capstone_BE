package ac.kr.mjc.capstone.domain.boardimage.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "board_image")
public class BoardImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", length = 255)
    private String filePath;

}
