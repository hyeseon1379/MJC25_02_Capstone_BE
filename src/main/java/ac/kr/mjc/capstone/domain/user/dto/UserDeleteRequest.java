package ac.kr.mjc.capstone.domain.user.dto;

public class UserDeleteRequest {
    private String password;

    // 데이터를 담고 꺼낼 수 있도록 Getter와 Setter가 필요합니다.
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}