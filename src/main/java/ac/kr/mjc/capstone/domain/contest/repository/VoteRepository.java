package ac.kr.mjc.capstone.domain.contest.repository;

import ac.kr.mjc.capstone.domain.contest.entity.ContestDetails;
import ac.kr.mjc.capstone.domain.contest.entity.Story;
import ac.kr.mjc.capstone.domain.contest.entity.Vote;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Boolean existsByStoryAndUser(Story story, UserEntity user);
    Boolean existsByContestDetailsAndUser(ContestDetails contestDetails, UserEntity user);
}
