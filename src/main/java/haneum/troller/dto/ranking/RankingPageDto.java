package haneum.troller.dto.ranking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Getter
@Setter
@NoArgsConstructor
public class RankingPageDto {
    private JSONArray player;
}
