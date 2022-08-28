package haneum.troller.domain;

import haneum.troller.Enum.LoginType;
import haneum.troller.common.BaseTimeEntityLocalDate;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import haneum.troller.common.BaseTimeEntity;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntityLocalDate {

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

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;


    @OneToMany(mappedBy = "member")
    private List<MemberChat> memberChats = new ArrayList<>();


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
                ", refreshToken='" + refreshToken + '\'' +
                ", type='" + type + '\'' +
                ", createDate=" + createDate +
                ", modifiedDate=" + modifiedDate +
                ", memberChats=" + memberChats +
                '}';
    }
}
