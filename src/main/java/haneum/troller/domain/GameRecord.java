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

    @Column(name = "full_record_rank", columnDefinition = "longtext")
    @Lob
    private String fullRecordRank;

    @Column(name = "most_champion_rank", columnDefinition = "longtext")
    @Lob
    private String mostChampionRank;

    @Column(name = "line_info_rank", columnDefinition = "longtext")
    @Lob
    private String lineInfoRank;



    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public GameRecord(String lolName, String lineInfo, String fullRecord, String mostChampion, String userInfo, String fullRecordRank, String mostChampionRank, String lineInfoRank, LocalDateTime createDate, LocalDateTime modifiedDate) {
        this.lolName = lolName;
        this.lineInfo = lineInfo;
        this.fullRecord = fullRecord;
        this.mostChampion = mostChampion;
        this.userInfo = userInfo;
        this.fullRecordRank = fullRecordRank;
        this.mostChampionRank = mostChampionRank;
        this.lineInfoRank = lineInfoRank;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
    }


    public GameRecord updateLineInfo(String lineInfo) {
        this.lineInfo = lineInfo;
        return this;
    }

    public GameRecord updateLineInfoRank(String lineInfoRank) {
        this.lineInfoRank = lineInfoRank;
        return this;
    }

    public GameRecord updateFullRecord(String fullRecord) {
        this.fullRecord = fullRecord;
        return this;
    }

    public GameRecord updateFullRecordRank(String fullRecordRank) {
        this.fullRecordRank = fullRecordRank;
        return this;
    }

    public GameRecord updateMostChampion(String mostChampion) {
        this.mostChampion = mostChampion;
        return this;
    }

    public GameRecord updateMostChampionRank(String mostChampionRank) {
        this.mostChampionRank = mostChampionRank;
        return this;
    }


    public GameRecord updateUserInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }



}
