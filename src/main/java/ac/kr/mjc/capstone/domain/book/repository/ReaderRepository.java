package ac.kr.mjc.capstone.domain.book.repository;

import ac.kr.mjc.capstone.domain.book.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByChildrenEntity_ChildId(Long childId);
}
