package ac.kr.mjc.capstone.domain.boardimage.repository;

import ac.kr.mjc.capstone.domain.boardimage.entity.BoardImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImageEntity, Long> {
}
