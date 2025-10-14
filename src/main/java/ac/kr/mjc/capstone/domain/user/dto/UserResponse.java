package ac.kr.mjc.capstone.domain.user.dto;

import ac.kr.mjc.capstone.domain.user.entity.Role;
import ac.kr.mjc.capstone.domain.user.entity.User;
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

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .birth(user.getBirth())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .color(user.getColor())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}
