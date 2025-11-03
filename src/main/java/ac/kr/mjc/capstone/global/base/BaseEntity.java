package ac.kr.mjc.capstone.global.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass  // 이 클래스를 상속받는 엔티티에 필드를 상속
@EntityListeners(AuditingEntityListener.class)  // JPA Auditing 활성화
@Getter
public abstract class BaseEntity {

    @CreatedDate  // 생성 시간 자동 입력
    @Column(name = "create_at", updatable = false)  // 업데이트 시 변경 안됨
    private LocalDateTime createAt;

    @LastModifiedDate  // 수정 시간 자동 업데이트
    @Column(name = "update_at")
    private LocalDateTime updateAt;
}