package haneum.troller.domain;

import haneum.troller.common.BaseTimeEntityLocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_info")
public class UserInfo extends BaseTimeEntityLocalDate {
    @Id
    private String lolName;
    private String encryptedLolName;
    private String tier;
    private String ranking;
    private String point;
    private String winRate;
    private String win;
    private String lose;
    private String icon;
    private String level;
    private String trollPossibility;
    private String cluster;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public UserInfo(String lolName, String encryptedLolName, String tier, String rank, String point, String winRate, String win, String lose, String icon, String level, String trollPossibility, LocalDateTime createDate, LocalDateTime modifiedDate, String cluster) {
        this.lolName = lolName;
        this.encryptedLolName = encryptedLolName;
        this.tier = tier;
        this.ranking = rank;
        this.point = point;
        this.winRate = winRate;
        this.win = win;
        this.lose = lose;
        this.icon = icon;
        this.level = level;
        this.trollPossibility = trollPossibility;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.cluster = cluster;
    }

    public UserInfo updateUserInfo(String lolName, String encryptedLolName, String tier, String rank, String point, String winRate, String win, String lose, String icon, String level, String trollPossibility) {
        this.lolName = lolName;
        this.encryptedLolName = encryptedLolName;
        this.tier = tier;
        this.ranking = rank;
        this.point = point;
        this.winRate = winRate;
        this.win = win;
        this.lose = lose;
        this.icon = icon;
        this.level = level;
        this.trollPossibility = trollPossibility;

        return this;
    }

    public UserInfo updateTrollPossibility(String trollPossibility) {
        this.trollPossibility = trollPossibility;
        return this;
    }

    public UserInfo updateCluster(String cluster) {
        this.cluster = cluster;
        return this;
    }




}
