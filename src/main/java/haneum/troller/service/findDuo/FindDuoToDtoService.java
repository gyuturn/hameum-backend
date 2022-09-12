package haneum.troller.service.findDuo;

import haneum.troller.common.apiKey.LolApiKey;
import haneum.troller.dto.findDuo.FindDuoResponseDto;
import haneum.troller.service.dataDragon.ChampionImgService;
import haneum.troller.service.fullSearch.GameRecord.GameTwentyRecord;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSearchSet;
import haneum.troller.service.fullSearch.linePreference.LinePreferenceService;
import haneum.troller.service.getRiotApi.GetRiotApi;
import haneum.troller.service.getRiotApi.GetRiotApiUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
public class FindDuoToDtoService {
    @Autowired
    private ChampionImgService championImgService;
    @Autowired
    private GetRiotApi getRiotApi;
    @Autowired
    private GetRiotApiUtil getRiotApiUtil;
    @Autowired
    private FullSearchSet fullSearchSet;
    @Autowired
    private FindDuoSetUtil findDuoSetUtil;

    public FindDuoResponseDto getFindDuoDto(String lolName) throws ParseException {
        FindDuoResponseDto findDuoDto = new FindDuoResponseDto();
        String userPid = getRiotApiUtil.getUserPid(lolName);
        ArrayList matchList = getRiotApiUtil.getMatchId(userPid, 20);
        ArrayList champions = new ArrayList<>();
        ArrayList retArray = new ArrayList();
        ArrayList lines = new ArrayList();
        GameTwentyRecord gameTwentyRecord = new GameTwentyRecord();
        setTierPoint(lolName, findDuoDto);
        for (int i = 0; i < 20; i++){
            setGameRecord((String)matchList.get(i), gameTwentyRecord, lines, champions);
        }
        setKdaWinRateDto(gameTwentyRecord, findDuoDto);
        findDuoSetUtil.setMostThree(champions, findDuoDto); // most three champ;
        findDuoDto.setLolName(lolName);
        findDuoDto.setFavorPosition(findDuoSetUtil.matchLinePreference(lines));
        return findDuoDto;
    }

    public void setGameRecord(String matchId,GameTwentyRecord twentyRecord, ArrayList lines ,ArrayList champions) throws org.json.simple.parser.ParseException {
        String response = getRiotApi.getResponseEntityByMatchId(matchId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response);
        JSONObject jsonObj = (JSONObject) obj;
        JSONObject info = (JSONObject) jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject user = getRiotApiUtil.getUserFromJson(participants);
        fullSearchSet.setKdaWinRateTwenty(user, twentyRecord);
        champions.add(user.get("championName")); // 20판에서 챔피언 기록.
        lines.add(user.get("teamPosition")); // 20판에서 라인 기록.
    }

    public void setKdaWinRateDto(GameTwentyRecord gameTwentyRecord, FindDuoResponseDto dto){
        dto.setKill(Integer.toString(gameTwentyRecord.getKill()));
        dto.setDeath(Integer.toString(gameTwentyRecord.getDeath()));
        dto.setAssist(Integer.toString(gameTwentyRecord.getAssist()));
        dto.setWin(Integer.toString(gameTwentyRecord.getWin()));
        dto.setLose(Integer.toString(gameTwentyRecord.getLose()));
        dto.setWinRate(Double.toString(gameTwentyRecord.getWinRate()));
    }

    public void setTierPoint(String lolName, FindDuoResponseDto findDuoDto) throws ParseException {
        ResponseEntity<String> response = getRiotApi.getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;
        String encryptedId = (String)jsonObj.get("id");

        ResponseEntity<String> response2 = getRiotApi.getResponseEntityByEncryptedUserId(encryptedId);
        obj = parser.parse(response2.getBody());
        JSONArray jsonArray = (JSONArray)obj;
        jsonObj = (JSONObject)jsonArray.get(0);

        findDuoDto.setTier(jsonObj.get("tier").toString());
        findDuoDto.setLeaguePoint(jsonObj.get("leaguePoints").toString());
    }
}
