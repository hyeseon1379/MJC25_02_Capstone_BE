package ac.kr.mjc.capstone.global.media.repository;

import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<ImageFileEntity, Long> {
}
