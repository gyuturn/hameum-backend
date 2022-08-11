package haneum.troller.dto.gameRecord;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Data
@NoArgsConstructor
public class GameRecordDto {
    private JSONObject latestTwentyRecords;

    private JSONArray gameRecord;
}
