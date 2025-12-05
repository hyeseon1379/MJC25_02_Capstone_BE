package ac.kr.mjc.capstone.domain.contest.repository;

import ac.kr.mjc.capstone.domain.contest.entity.Contest;
import ac.kr.mjc.capstone.domain.contest.entity.ContestDetails;
import ac.kr.mjc.capstone.domain.contest.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestDetailsRepository extends JpaRepository<ContestDetails, Long> {
    List<ContestDetails> findByContestOrderByStartDateDesc(Contest contest);

    // 특정 Contest의 모든 라운드 조회
    List<ContestDetails> findByContest(Contest contest);

    // 특정 Contest의 특정 라운드 조회
    Optional<ContestDetails> findByContestAndRound(Contest contest, Round round);
}
