package haneum.troller.service.fullSearch.linePreference;

import haneum.troller.common.apiKey.LolApiKey;
import haneum.troller.dto.linePrefer.LinePreferenceDto;
import haneum.troller.service.fullSearch.GameRecord.GameRecordService;
import haneum.troller.service.getRiotApi.GetRiotApi;
import haneum.troller.service.getRiotApi.GetRiotApiUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class LinePreferenceService {
    @Autowired
    public GetRiotApi getRiotApi;

    @Autowired
    public GetRiotApiUtil getRiotApiUtil;

    public LinePreferenceDto getLinePreferenceDto(String lolName, int count, String type) throws ParseException, IOException {

        LinePreferenceDto linePreferenceDto = new LinePreferenceDto();
        ArrayList<String> position = new ArrayList<>(count);
        String userPid = null;
        try {
                 userPid = getRiotApiUtil.getUserPid(lolName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList matchList = getRiotApiUtil.machineLearningGetMatchId(userPid, count, type);
        for (int i = 0; i < count; i++){ // 원래는 i = 0 i < 20
            settingLinePreference((String)matchList.get(i), position);
        }
        linePreferenceDto.setFirstLinePreference((String)matchLinePreference(position).get("firstLinePreference"));
        linePreferenceDto.setFirstLinePlayed((String)matchLinePreference(position).get("firstLinePlayed"));
        linePreferenceDto.setSecondLinePreference((String)matchLinePreference(position).get("secondLinePreference"));
        linePreferenceDto.setSecondLinePlayed((String)matchLinePreference(position).get("secondLinePlayed"));
        return linePreferenceDto;
    }
    public void settingLinePreference(String matchId, ArrayList position) throws ParseException, IOException {
        String response = getRiotApi.getResponseEntityByMatchId(matchId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response);
        JSONObject jsonObj = (JSONObject)obj;
        JSONObject info = (JSONObject)jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject user = getRiotApiUtil.getUserFromJson(participants);
        position.add(user.get("teamPosition"));
        return ;
    }

    public JSONObject matchLinePreference(ArrayList position){
        JSONObject linePreference = new JSONObject();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("TOP", 0);
        map.put("JUNGLE", 0);
        map.put("MID", 0);
        map.put("BOTTOM", 0);
        map.put("UTILITY", 0);
        for (Object line : position){
            switch ((String)line){

                case "TOP":
                    map.replace("TOP", map.get("TOP") + 1);
                    break;
                case "JUNGLE":
                    map.replace("JUNGLE", map.get("JUNGLE") + 1);
                    break;
                case "MID":
                    map.replace("MID", map.get("MID") + 1);
                    break;
                case "BOTTOM":
                    map.replace("BOTTOM", map.get("BOTTOM") + 1);
                    break;
                case "UTILITY":
                    map.replace("UTILITY", map.get("UTILITY") + 1);
                    break;
            }
        }
        List<String> keySet = new ArrayList<>(map.keySet());
        keySet.sort((o1, o2) -> map.get(o2).compareTo(map.get(o1)));
        int i = 0;
        int winRate = 0;
        for (String key : keySet) {
            if (i == 0) {
                winRate = (int)map.get(key);
                linePreference.put("firstLinePreference", key);
                linePreference.put("firstLinePlayed", String.valueOf(winRate));
            }
            else {
                winRate = (int)map.get(key);
                linePreference.put("secondLinePreference", key);
                linePreference.put("secondLinePlayed", String.valueOf(winRate));
            }
            i++;
            if (i == 2)
                break;
        }
        return linePreference;
    }

}


