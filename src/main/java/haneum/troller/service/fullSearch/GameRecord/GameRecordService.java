package haneum.troller.service.fullSearch.GameRecord;


import haneum.troller.dto.gameRecord.GameRecordDto;
import haneum.troller.service.dataDragon.ChampionImgService;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSearchSet;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
import haneum.troller.service.fullSearch.mostThreeChampion.GameMostChampionRecord;
import haneum.troller.service.getRiotApi.GetRiotApi;
import haneum.troller.service.getRiotApi.GetRiotApiUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

@Service
public class GameRecordService {
    @Autowired
    private ChampionImgService championImgService;
    @Autowired
    private GetRiotApi getRiotApi;
    @Autowired
    private GetRiotApiUtil getRiotApiUtil;
    @Autowired
    private FullSerachUtil fullSerachUtil;
    @Autowired
    private FullSearchSet fullSearchSet;
    @Autowired
    private GetJsonFromUrl getJsonFromUrl;
    @Autowired
    private GameRune gameRune;
    @Autowired
    private GameSpell gameSpell;
    @Autowired
    private GameItem gameItem;
    @Autowired
    private MatchDataSet matchDataSet;
    @Autowired
    private MatchPlayer matchPlayer;


    public GameRecordDto getGameRecord(String lolName, int count) throws ParseException, org.json.simple.parser.ParseException, IOException {

        GameRecordDto gameRecordDto = new GameRecordDto();
        JSONArray gameRecordArray = new JSONArray();
        JSONObject gameTwentyRecordObject = new JSONObject();
        GameTwentyRecord gameTwentyRecord = new GameTwentyRecord();
        GameMostChampionRecord gameMostChampionRecord;
        ArrayList<Class>champion = new ArrayList<>(20);

        JSONArray runeFile = getJsonFromUrl.readJsonArrayFromUrl("https://ddragon.leagueoflegends.com/cdn/12.15.1/data/ko_KR/runesReforged.json");
        JSONObject spellFile = getJsonFromUrl.readJsonObjFromUrl("https://ddragon.leagueoflegends.com/cdn/12.15.1/data/ko_KR/summoner.json");
        JSONObject itemFile = getJsonFromUrl.readJsonObjFromUrl("https://ddragon.leagueoflegends.com/cdn/12.15.1/data/ko_KR/item.json");
        String userPid = getRiotApiUtil.getUserPid(lolName);
        ArrayList matchList = getRiotApiUtil.getMatchId(userPid, count);
        for (int i = 0; i < count; i++){
            gameRecordArray.add(setGameRecord((String) matchList.get(i), gameTwentyRecord, runeFile,
                    spellFile, itemFile,lolName));
        }
        gameTwentyRecord.setTotalKillRelated(Math.round(gameTwentyRecord.getTotalKillRelated() / 20));
        gameRecordDto.setLatestTwentyRecords(fullSearchSet.setKdaWinRateDto(gameTwentyRecord, gameTwentyRecordObject));
        gameRecordDto.setGameRecord(gameRecordArray);
        return gameRecordDto;
    }

    public JSONObject setGameRecord(String matchId , GameTwentyRecord twentyRecord, JSONArray rune, JSONObject spell,
                                    JSONObject item ,String lolName) throws org.json.simple.parser.ParseException, IOException {
        String response = getRiotApi.getResponseEntityByMatchId(matchId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response);
        JSONObject jsonObj = (JSONObject)obj;
        JSONObject info = (JSONObject)jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject userRecord = new JSONObject();
        JSONObject user = getUserFromJson(participants, lolName);
        userRecord.put("gameMode", (String)info.get("gameMode"));
        userRecord.put("matchId", matchId);
        int hitDamageToChampion = fullSerachUtil.ParseToInt(user, "totalDamageDealtToChampions");
        userRecord.put("hitDamageToChampion", hitDamageToChampion);
        String championName = (String)user.get("championName");
        userRecord.put("championName", championName);
        userRecord.put("championUI", championImgService.getChampionImg(championName));
        fullSearchSet.setKdaWinRateTwenty(user, twentyRecord);
        matchDataSet.matchKdaAndWinRecord(participants, user, userRecord);
        matchDataSet.matchMetaDataSetting(user, userRecord, rune, spell, item);
        matchPlayer.getKillRate(info, user, userRecord, twentyRecord);
        int playTime = matchDataSet.matchPlayTime(info, userRecord);
        matchDataSet.matchCsAndWardAndLevel(user, userRecord, playTime);
        matchPlayer.getAvgTier(lolName, userRecord);
        JSONArray players = matchPlayer.setPlayers(user, participants); //10명의 사용자 정보 링크와 평균 티어만 구하는 함수.
        userRecord.put("players", players);
        return userRecord;
    }

    public JSONObject getUserFromJson(JSONArray participants, String lolName){
        JSONObject user = null;
        String name = null;
        for (int i = 0; i < 10; i++){
            user = (JSONObject) participants.get(i);
            name = (String)user.get("summonerName");
            if (lolName.compareTo(name) == 0)
                break ;
        }
        return user;
    }
}
