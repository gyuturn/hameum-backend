package haneum.troller.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.engine.profile.Fetch;
//import org.springframework.data.annotation.Id;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;


@Entity
//@Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

//   @NotBlank
    private String lolName;

    private ArrayList favorChampions;

    private String favorPosition;

    private String tier;

    private int win;

    private int lose;

    private int killing;

    private int death;

    private int assist;

    private int vaildTime;

    private Boolean mike;

    private String title;

    private String content;


    @Builder
    public Board(long id, String lolName, ArrayList favorChampions, String favorPosition, String tier,
                 int win, int lose, int killing, int death, int assist, int vaildTime, Boolean mike,
                 String title, String content){
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
        this.vaildTime = vaildTime;
        this.mike = mike;
        this.title = title;
        this.content = content;
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
                .vaildTime(vaildTime)
                .mike(mike)
                .title(title)
                .content(content)
                .build();
    }
}

