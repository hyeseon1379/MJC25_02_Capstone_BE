package ac.kr.mjc.capstone.domain.packaze.repository;

import ac.kr.mjc.capstone.domain.packaze.entity.Packaze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackazeRepository extends JpaRepository<Packaze, Long> {
    List<Packaze> findAllByIsActiveTrue();
    Optional<Packaze> findByIdAndIsActiveTrue(Long id);
}