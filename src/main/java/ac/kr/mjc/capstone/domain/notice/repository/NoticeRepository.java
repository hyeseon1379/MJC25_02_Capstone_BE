package ac.kr.mjc.capstone.domain.notice.repository;

import ac.kr.mjc.capstone.domain.notice.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
    // 최신순으로 공지사항 조회
    Page<NoticeEntity> findAllByOrderByCreateAtDesc(Pageable pageable);
}
