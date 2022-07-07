package haneum.troller.dto.myPage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Getter
@NoArgsConstructor
@Setter
public class MyPageDto {
    private String encryptedLolName;

    private String name;

    private String tier;

    private String rank;

    private String point;

//    private String tierImg;

    private String winRate;

    private String TrollPossibility="0";// 아직 미구현

    private String win;

    private String lose;

    private String icon;

    private String level;

    private Image tierImg;


}
