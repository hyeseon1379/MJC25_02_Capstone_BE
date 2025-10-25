package ac.kr.mjc.capstone.domain.user.dto;

import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String username;
    private LocalDate birth;
    private String phone;
    private String nickname;
    private String color;
    private String address;
    private Role role;

    public static UserResponse from(UserEntity userEntity) {
        return UserResponse.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .birth(userEntity.getBirth())
                .phone(userEntity.getPhone())
                .nickname(userEntity.getNickname())
                .color(userEntity.getColor())
                .address(userEntity.getAddress())
                .role(userEntity.getRole())
                .build();
    }
}
