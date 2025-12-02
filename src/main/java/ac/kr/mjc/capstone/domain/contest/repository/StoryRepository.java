package ac.kr.mjc.capstone.domain.contest.repository;

import ac.kr.mjc.capstone.domain.contest.entity.ContestDetails;
import ac.kr.mjc.capstone.domain.contest.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByContestDetailsOrderByCreateAtDesc(ContestDetails contestDetails);

    // 특정 라운드에서 투표수 1위 Story 조회
    Optional<Story> findTopByContestDetailsOrderByVoteCountDesc(ContestDetails contestDetails);
}
