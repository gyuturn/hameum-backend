package haneum.troller.domain;

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
    @NotBlank
    private String password;
    @NotBlank
    private String lolName;

    private String refreshToken;

    @Builder
    public Member(Long memberId, String email, String password, String lolName,String refreshToken) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.lolName = lolName;
        this.refreshToken = refreshToken;
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
