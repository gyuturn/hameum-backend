package haneum.troller.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @GeneratedValue
    @Id
    @Column(name = "member_id")
    private Long memberId;

    private String email;

    private String password;

    private String lolName;

    @Builder
    public Member(Long memberId, String email, String password, String lolName) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.lolName = lolName;
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
