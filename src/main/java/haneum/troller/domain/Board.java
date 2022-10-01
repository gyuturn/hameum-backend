package haneum.troller.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
import javax.persistence.*;
import java.util.ArrayList;


@Entity
@NoArgsConstructor
//@Table(name = "board")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

//   @NotBlank
    private String lolName;

    @Lob
    private ArrayList favorChampions;

    private String favorPosition;

    private String tier;

    private int win;

    private int lose;

    private double killing;

    private double death;

    private double assist;

    private String positionData;

    private Boolean mike;

    private String title;

    private String content;

    private long timeStamp;

    private String position;

    private double kda;

    @Builder
    public Board(long id, String lolName, ArrayList favorChampions, String favorPosition, String tier,
                 int win, int lose, double killing, double death, double assist, String positionData, Boolean mike,
                 String title, String content, long timeStamp, String position, double kda
    ){
        this.id = id;
        this.lolName = lolName;
        this.favorChampions = favorChampions;
        this.favorPosition = favorPosition;
        this.tier = tier;
        this.win = win;
        this.lose = lose;
        this.killing = killing;
        this.death =death;
        this.assist = assist;
        this.positionData = positionData;
        this.mike = mike;
        this.title = title;
        this.content = content;
        this.timeStamp = timeStamp;
        this.position = position;
        this.kda = kda;
    }

    public Board toEntity(){
        return Board.builder()
                .id(id)
                .lolName(lolName)
                .favorChampions(favorChampions)
                .favorPosition(favorPosition)
                .tier(tier)
                .win(win)
                .lose(lose)
                .killing(killing)
                .death(death)
                .assist(assist)
                .positionData(positionData)
                .mike(mike)
                .title(title)
                .content(content)
                .timeStamp(timeStamp)
                .position(position)
                .kda(kda)
                .build();
    }
}

