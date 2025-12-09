package ac.kr.mjc.capstone.domain.share.repository;

import ac.kr.mjc.capstone.domain.share.entity.MeetStatus;
import ac.kr.mjc.capstone.domain.share.entity.ShareBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareBoardRepository extends JpaRepository<ShareBoardEntity, Long> {

    /**
     * 제목 또는 내용에 키워드가 포함된 게시글 검색
     */
    @Query("SELECT s FROM ShareBoardEntity s " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR s.title LIKE %:keyword% OR s.content LIKE %:keyword%) " +
            "AND (:status IS NULL OR s.meetStatus = :status)")
    Page<ShareBoardEntity> findByKeywordAndStatus(
            @Param("keyword") String keyword,
            @Param("status") MeetStatus status,
            Pageable pageable
    );

    /**
     * 특정 사용자의 게시글 목록 조회
     */
    Page<ShareBoardEntity> findByUserUserId(Long userId, Pageable pageable);
}
