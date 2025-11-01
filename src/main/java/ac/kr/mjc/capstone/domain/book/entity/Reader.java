package ac.kr.mjc.capstone.domain.book.entity;

import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reader")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reader_id")
    private Long readerId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private ChildrenEntity childrenEntity;

    @Enumerated(EnumType.STRING)
    private ReaderType readerType;

    public String getDisplayName() {
        return readerType == ReaderType.ADULT ? userEntity.getNickname() : childrenEntity.getChildName();
    }

    public String getProfileImageUrl() {
        return readerType == ReaderType.ADULT ? userEntity.getProfileImg() : childrenEntity.getProfileImg();
    }
}
