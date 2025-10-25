package ac.kr.mjc.capstone.domain.children.repository;

import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildrenRepository extends JpaRepository<ChildrenEntity, Long> {

}
