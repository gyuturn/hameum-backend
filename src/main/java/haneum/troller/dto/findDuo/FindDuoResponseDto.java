package haneum.troller.dto.findDuo;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Data
public class FindDuoResponseDto {
    private String lolName;

//    private ArrayList<String> favorChampions;

    private String champion1;

    private String champion2;

    private String champion3;

    private String tier; //png url

    private String win; // setKdaRate

    private String lose; // setKdaRate

    private String kill; // setKdaRate

    private String death; // setKdaRate

    private String assist; // setKdaRate

    private String winRate; // 자동적으로 gameRecordService에서 구함.

    private String leaguePoint; //setTierPoint

    private String favorPosition; //matchLinePreference

    private String position;

    private Double kda;
}
