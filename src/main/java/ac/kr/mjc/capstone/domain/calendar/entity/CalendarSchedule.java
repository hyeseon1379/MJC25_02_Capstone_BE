package ac.kr.mjc.capstone.domain.calendar.entity;

import ac.kr.mjc.capstone.domain.book.entity.Book;
import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_schedule")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CalendarSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private ChildrenEntity child;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.READING;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상태 자동 계산
     */
    public ScheduleStatus calculateStatus() {
        LocalDate today = LocalDate.now();
        
        if (startDate.isAfter(today)) {
            return ScheduleStatus.TO_READ;
        } else if (endDate != null && endDate.isBefore(today)) {
            return ScheduleStatus.COMPLETED;
        } else {
            return ScheduleStatus.READING;
        }
    }

    /**
     * 일정 수정
     */
    public void updateSchedule(ChildrenEntity child, LocalDate startDate, LocalDate endDate) {
        this.child = child;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = calculateStatus();
    }

    /**
     * 독자 이름 반환 (본인이면 사용자 이름, 자녀면 자녀 이름)
     */
    public String getReaderName() {
        if (child != null) {
            return child.getChildName();
        }
        return user.getNickname() != null ? user.getNickname() : user.getUsername();
    }

    /**
     * 독자 색상 반환
     */
    public String getReaderColor() {
        if (child != null) {
            return child.getColor();
        }
        return user.getColor();
    }
}
