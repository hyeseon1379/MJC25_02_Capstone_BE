package ac.kr.mjc.capstone.domain.reply.repository;

import ac.kr.mjc.capstone.domain.reply.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    
    // 특정 게시글의 모든 댓글 조회 (생성일 오름차순)
    @Query("SELECT r FROM ReplyEntity r " +
           "JOIN FETCH r.user " +
           "WHERE r.board.boardId = :boardId " +
           "ORDER BY r.createAt ASC")
    Page<ReplyEntity> findByBoardIdWithUser(@Param("boardId") Long boardId, Pageable pageable);
    
    // 특정 게시글의 댓글 개수
    long countByBoardBoardId(Long boardId);
}
