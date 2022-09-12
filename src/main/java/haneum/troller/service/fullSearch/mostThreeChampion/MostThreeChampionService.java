package haneum.troller.service.fullSearch.mostThreeChampion;

import haneum.troller.common.apiKey.LolApiKey;
import haneum.troller.dto.mostChampion.MostThreeChampionDto;
import haneum.troller.service.dataDragon.ChampionImgService;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSearchSet;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
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
import java.util.Collections;

import static java.lang.Boolean.TRUE;

@Service
public class MostThreeChampionService {

    @Autowired
    public ChampionImgService championImgService;
    @Autowired
    public GetRiotApi getRiotApi;
    @Autowired
    public GetRiotApiUtil getRiotApiUtil;
    @Autowired
    public MostChampUtil mostChampUtil;
    @Autowired
    public FullSerachUtil fullSerachUtil;
    @Autowired
    public FullSearchSet fullSearchSet;

    public MostThreeChampionDto getMostThreeChampionDto(String lolName, int count) throws ParseException, IOException {
        MostThreeChampionDto mostThreeChampionDto = new MostThreeChampionDto();
        ArrayList<HashMap>champions = new ArrayList<>();
        String userPid = null;
        try {
            userPid = getRiotApiUtil.getUserPid(lolName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList matchList = getRiotApiUtil.getMatchId(userPid, count);
        int cnt = 0;
        for (int i = 0; i < count; i++){
            settingMostChampion((String)matchList.get(i), champions, lolName);
            cnt++;
        }
        setMostThreeChampion(champions, mostThreeChampionDto);
        return mostThreeChampionDto;
    }

    public void setMostThreeChampion(ArrayList champions, MostThreeChampionDto dto){
        JSONArray returnArray = new JSONArray();
        JSONObject mostOne = new JSONObject();
        JSONObject second = new JSONObject();
        JSONObject third = new JSONObject();
        Collections.sort(champions, Collections.reverseOrder());
        if (champions.size() == 1){
            returnArray.add(setDto(champions, mostOne, 1));
            returnArray.add(second);
            returnArray.add(third);
        }
        else if(champions.size() == 2){
            returnArray.add(setDto(champions, mostOne, 1));
            returnArray.add(setDto(champions, second, 2));
            returnArray.add(third);
        }
        else{
            returnArray.add(setDto(champions, mostOne, 1));
            returnArray.add(setDto(champions, second, 2));
            returnArray.add(setDto(champions, third, 3));
        }
        dto.setMostThreeChampion(returnArray);
    }

    public JSONObject setDto(ArrayList champions, JSONObject champ, int flag){
        GameMostChampionRecord champRecord = (GameMostChampionRecord) champions.get(flag - 1);
        champ.put("gamePlayed", Integer.toString(champRecord.getGamePlayed()));
        champ.put("championName", champRecord.getChampionName());
        champ.put("win", Integer.toString(champRecord.getWin()));
        champ.put("lose", Integer.toString(champRecord.getLose()));
        champ.put("draw", Integer.toString(champRecord.getDraw()));
        champ.put("winRate", Double.toString(champRecord.getCalculatedWinRate()));
        champ.put("kill", Double.toString(champRecord.getAvgKda(champRecord.getKill())));
        champ.put("death", Double.toString(champRecord.getAvgKda(champRecord.getDeath())));
        champ.put("assist", Double.toString(champRecord.getAvgKda(champRecord.getAssist())));
        champ.put("kda", Double.toString(champRecord.getCalculatedKda()));
        champ.put("cs", Integer.toString(champRecord.getCs()));
        champ.put("csPerMinutes", Double.toString(champRecord.getCsPerMinutes()));
        champ.put("championUi", champRecord.getUi());
        return champ;
    }

    // 원래는 i 대신에 String matchId
    public void settingMostChampion(String matchId, ArrayList champions, String lolName) throws ParseException, IOException {
        String response = getRiotApi.getResponseEntityByMatchId(matchId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response);
        JSONObject jsonObj = (JSONObject)obj;
        JSONObject info = (JSONObject)jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject user = mostChampUtil.getUserFromJson(participants, lolName);
        String className = (String)user.get("championName");
        int playTime = fullSerachUtil.ParseToInt(info, "gameDuration");
        int index;
        GameMostChampionRecord gameMostChampionRecord = null;
        if (mostChampUtil.isContain(champions, className)) {
            index = mostChampUtil.getIndexOf(champions, className);
            setChampionClass(user, (GameMostChampionRecord) champions.get(index), playTime, className);
        }
        else{
            gameMostChampionRecord = new GameMostChampionRecord();
            setChampionClass(user, gameMostChampionRecord, playTime, className);
            gameMostChampionRecord.setUi(championImgService.getChampionImg(className));
            champions.add(gameMostChampionRecord);
        }
    }

    public void setChampionClass(JSONObject user ,GameMostChampionRecord gameMostChampionRecord, int playTime, String className){
        gameMostChampionRecord.setGamePlayed(gameMostChampionRecord.getGamePlayed() + 1);
        fullSearchSet.setKdaWinRate(user, gameMostChampionRecord);
        gameMostChampionRecord.setChampionName(className);
        fullSearchSet.setCs(user, gameMostChampionRecord, playTime);
    }
}