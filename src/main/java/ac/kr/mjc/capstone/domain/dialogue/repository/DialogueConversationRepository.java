package ac.kr.mjc.capstone.domain.dialogue.repository;

import ac.kr.mjc.capstone.domain.dialogue.entity.DialogueConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DialogueConversationRepository extends JpaRepository<DialogueConversation, Long> {

    // 사용자의 모든 대화 목록 조회 (삭제되지 않은 것만, 최신순)
    Page<DialogueConversation> findByUserUserIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long userId, Pageable pageable);

    // 특정 도서의 대화 목록 조회
    Page<DialogueConversation> findByUserUserIdAndBookBookIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long userId, Long bookId, Pageable pageable);

    // 대화 상세 조회 (삭제되지 않은 것만)
    Optional<DialogueConversation> findByConversationIdAndIsDeletedFalse(Long conversationId);

    // 대화 상세 조회 (사용자 확인 포함)
    Optional<DialogueConversation> findByConversationIdAndUserUserIdAndIsDeletedFalse(
            Long conversationId, Long userId);

    // 감정 필터링 조회
    @Query("SELECT DISTINCT dc FROM DialogueConversation dc " +
            "JOIN dc.emotions e " +
            "WHERE dc.user.userId = :userId " +
            "AND dc.isDeleted = false " +
            "AND e.emotionType IN :emotions " +
            "ORDER BY dc.createdAt DESC")
    Page<DialogueConversation> findByUserIdAndEmotions(
            @Param("userId") Long userId,
            @Param("emotions") List<String> emotions,
            Pageable pageable);

    // 키워드 검색 (제목, 내용)
    @Query("SELECT dc FROM DialogueConversation dc " +
            "WHERE dc.user.userId = :userId " +
            "AND dc.isDeleted = false " +
            "AND (LOWER(dc.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(dc.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY dc.createdAt DESC")
    Page<DialogueConversation> searchByKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // 날짜 범위 검색
    @Query("SELECT dc FROM DialogueConversation dc " +
            "WHERE dc.user.userId = :userId " +
            "AND dc.isDeleted = false " +
            "AND dc.createdAt >= :startDate " +
            "AND dc.createdAt <= :endDate " +
            "ORDER BY dc.createdAt DESC")
    Page<DialogueConversation> findByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 복합 검색 (키워드 + 날짜 + 감정)
    @Query("SELECT DISTINCT dc FROM DialogueConversation dc " +
            "LEFT JOIN dc.emotions e " +
            "WHERE dc.user.userId = :userId " +
            "AND dc.isDeleted = false " +
            "AND (:keyword IS NULL OR LOWER(dc.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(dc.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR dc.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR dc.createdAt <= :endDate) " +
            "AND (:emotions IS NULL OR e.emotionType IN :emotions) " +
            "ORDER BY dc.createdAt DESC")
    Page<DialogueConversation> searchWithFilters(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("emotions") List<String> emotions,
            Pageable pageable);
}
