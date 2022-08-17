package haneum.troller.dto.findDuo;


import lombok.Data;
import java.util.ArrayList;

@Data
public class FindDuoDto {
    private String lolName;

    private ArrayList<String> mostChampion;

    private String tier; //png url

    private String win; // setKdaRate

    private String lose; // setKdaRate

    private String kill; // setKdaRate

    private String death; // setKdaRate

    private String assist; // setKdaRate

    private String winRate; // 자동적으로 gameRecordService에서 구함.

    private String kdaRate;

    private String leaguePoint; //setTierPoint

    private String favorPositionDesc; //matchLinePreference
}
