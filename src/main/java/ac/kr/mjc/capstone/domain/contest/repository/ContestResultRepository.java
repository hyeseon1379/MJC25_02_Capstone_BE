package ac.kr.mjc.capstone.domain.contest.repository;

import ac.kr.mjc.capstone.domain.contest.entity.Contest;
import ac.kr.mjc.capstone.domain.contest.entity.ContestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestResultRepository extends JpaRepository<ContestResult, Long> {
    List<ContestResult> findByContestOrderByCreateAtDesc(Contest contest);
    Optional<ContestResult> findByContestAndTitle(Contest contest, String title);
}
