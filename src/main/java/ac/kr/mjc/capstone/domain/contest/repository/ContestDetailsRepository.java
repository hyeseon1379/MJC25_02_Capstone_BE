package ac.kr.mjc.capstone.domain.contest.repository;

import ac.kr.mjc.capstone.domain.contest.entity.Contest;
import ac.kr.mjc.capstone.domain.contest.entity.ContestDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestDetailsRepository extends JpaRepository<ContestDetails, Long> {
    List<ContestDetails> findByContestOrderByStartDateDesc(Contest contest);
}
