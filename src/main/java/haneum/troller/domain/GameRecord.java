package haneum.troller.domain;

import haneum.troller.common.BaseTimeEntity;
import haneum.troller.common.BaseTimeEntityLocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "game_record")
@DynamicUpdate
public class GameRecord extends BaseTimeEntityLocalDate {

    @Id
    private String lolName;

    @Column(name = "line_info", columnDefinition = "longtext")
    @Lob
    private String lineInfo;

    @Column(name = "full_record", columnDefinition = "longtext")
    @Lob
    private String fullRecord;

    @Column(name = "most_champion", columnDefinition = "longtext")
    @Lob
    private String mostChampion;

    @Column(name = "user_info", columnDefinition = "longtext")
    @Lob
    private String userInfo;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;


    @Builder
    public GameRecord(String lolName, String lineInfo, String fullRecord, String mostChampion, String userInfo) {
        this.lolName = lolName;
        this.lineInfo = lineInfo;
        this.fullRecord = fullRecord;
        this.mostChampion = mostChampion;
        this.userInfo = userInfo;
    }

    public GameRecord updateLineInfo(String lineInfo) {
        this.lineInfo = lineInfo;
        return this;
    }

    public GameRecord updateFullRecord(String fullRecord) {
        this.fullRecord = fullRecord;
        return this;
    }

    public GameRecord updateMostChampion(String mostChampion) {
        this.mostChampion = mostChampion;
        return this;
    }


    public GameRecord updateUserInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

}
