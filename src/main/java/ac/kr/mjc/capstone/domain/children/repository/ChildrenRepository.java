package ac.kr.mjc.capstone.domain.children.repository;

import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildrenRepository extends JpaRepository<ChildrenEntity, Long> {
    List<ChildrenEntity> findAllByUserEntity_UserId(Long UserId);
    void deleteAllByUserEntity_UserId(Long userId);
    ChildrenEntity findByUserEntity_UserId(Long UserId);

}
