package haneum.troller.domain;

import haneum.troller.Enum.LoginType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @GeneratedValue
    @Id
    @Column(name = "member_id")
    private Long memberId;

    @NotBlank
    private String email;

    private String password;

    private String lolName;

    private String refreshToken;

    private String type = LoginType.NORMAL.label();


    @Builder
    public Member(Long memberId, String email, String password, String lolName,String refreshToken) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.lolName = lolName;
        this.refreshToken = refreshToken;
    }

    public void updateLoginType(String type) {
        this.type=type;
    }


    public Member updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }


    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lolName='" + lolName + '\'' +
                '}';
    }
}
