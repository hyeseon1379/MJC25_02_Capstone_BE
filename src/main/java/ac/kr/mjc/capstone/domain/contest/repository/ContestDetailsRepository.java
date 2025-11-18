package ac.kr.mjc.capstone.domain.contest.repository;

import ac.kr.mjc.capstone.domain.contest.entity.ContestDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestDetailsRepository extends JpaRepository<ContestDetails, Long> {
}
