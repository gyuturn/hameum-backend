package haneum.troller.service;

import haneum.troller.common.config.apiKey.LolApiKey;
import haneum.troller.dto.gameRecord.GameRecordDto;
import haneum.troller.service.dataDragon.ChampionImgService;
import io.swagger.v3.core.util.Json;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.time.Instant;
import java.io.FileReader;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class GameRecordService {
    @Autowired
    private ChampionImgService championImgService;

    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    private static final String ApiKey= LolApiKey.API_KEY;
    public LinePreferenceService linePreferenceService;

    public GameRecordDto getGameRecord(String lolName) throws ParseException, org.json.simple.parser.ParseException, IOException {

        GameRecordDto gameRecordDto = new GameRecordDto();
        JSONArray gameRecordArray = new JSONArray();
        JSONArray threeMostChampionArray = new JSONArray();
        JSONObject gameTwentyRecordObject = new JSONObject();
        GameTwentyRecord gameTwentyRecord = new GameTwentyRecord();
        GameMostChampionRecord gameMostChampionRecord;
        gameMostChampionRecord = new GameMostChampionRecord();
        ArrayList<Class>champion = new ArrayList<>(20);
        ArrayList<String>position = new ArrayList<>(20);
        String userPid = getUserPid(lolName);
        ArrayList matchList = getMatchId(userPid);
        for (int i = 1; i < 9; i++){
        //    gameRecordArray.add(setGameRecord((String) matchList.get(i), gameMostChampionRecord, gameTwentyRecord, champion));
            gameRecordArray.add(setGameRecord(i, gameMostChampionRecord, gameTwentyRecord, champion, lolName));
        }
        gameRecordDto.setLatestTwentyRecords(setKdaWinRateDto(gameTwentyRecord, gameTwentyRecordObject));
        gameRecordDto.setGameRecord(gameRecordArray);
        return gameRecordDto;
    }

    // 원래는 i 대신 String matchId 가 와야 함
    public JSONObject setGameRecord(int i ,GameMostChampionRecord mostChampion, GameTwentyRecord twentyRecord, ArrayList champion, String lolName) throws org.json.simple.parser.ParseException, IOException {
    //    ResponseEntity<String>response = getResponseEntityByMatchId(matchId);

        FileReader reader = new FileReader("/Users/ojeongmin/Documents/lol_json/test" + Integer.toString(i) + ".json");
        JSONParser parser = new JSONParser();
    //    Object obj = parser.parse(response.getBody());
        Object obj = parser.parse(reader);
        JSONObject jsonObj = (JSONObject)obj;
        JSONObject info = (JSONObject)jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject userRecord = new JSONObject();
        JSONObject user = getUserFromJson(participants, lolName);
        userRecord.put("gameMode", (String)info.get("gameMode"));
        setKdaWinRate(user, twentyRecord);
        matchKdaAndWinRecord(participants, user, userRecord);
        matchRuneAndSpellSet(user, userRecord);
        getKillRate(info, user, userRecord);
        int playTime = matchPlayTime(info, userRecord);
        matchCsAndWard(user, userRecord, playTime);
        JSONArray players = setPlayers(user, participants); //10명의 사용자 정보 링크와 평균 티어만 구하는 함수.
        userRecord.put("players", players);
        return userRecord;
    }

    public JSONArray setPlayers(JSONObject user,JSONArray participants) throws org.json.simple.parser.ParseException {
        double avgTier = 0; // save average tier of players
        JSONArray players = new JSONArray();
        for (int i = 0; i < 10; i++){
            avgTier += (double)getPlayers((JSONObject)participants.get(i), players, i); // get player's info
        }
        avgTier /= 10;
        user.put("averageTier", IntToTier((int)avgTier));
        return players;
    }

    public int getPlayers(JSONObject participant, JSONArray players, int teamIndex) throws org.json.simple.parser.ParseException {
        int tierToInteger = 0;
        String summonerId;
        JSONObject player = new JSONObject();
        player.put("lolName", (String)participant.get("summonerName"));
        String championName = (String)participant.get("championName");
        player.put("championName", (String)championName);
        player.put("championImg", championImgService.getChampionImg(championName));
        player.put("Position", (String)participant.get("teamPosition"));
        if (teamIndex < 5)
            player.put("team", "Blue");
        else
            player.put("team", "Red");
        summonerId = (String)participant.get("summonerId");
        tierToInteger = getAvgTier(summonerId, player);
        players.add(player);
        return tierToInteger;
    }

    public int TierToInt(String tier, String rank){
        int point = 0;

        if (tier.equals("IRON"))
            point = 0;
        else if (tier.equals("BRONZE"))
            point = 4;
        else if (tier.equals("SILVER"))
            point = 8;
        else if (tier.equals("GOLD"))
            point = 12;
        else if (tier.equals("PLATINUM"))
            point = 16;
        else if (tier.equals("DIAMOND"))
            point = 20;
        else if (tier.equals("MASTER"))
            point = 26;
        else if (tier.equals("GRANDMASTER"))
            point = 36;
        else if (tier.equals("CHALLENGER"))
            point = 46;

        if (rank.equals("IV"))
            point += 1;
        else if (rank.equals("III"))
            point += 2;
        else if (rank.equals("II"))
            point += 3;
        else if (rank.equals("I"))
            point += 4;
        return point;
    }

    public String IntToTier(int point){
        if (point >= 0 && point <= 4)
            return "IRON";
        else if (point >= 5 && point <= 8)
            return "BRONZE";
        else if (point >= 9 && point <= 12)
            return "SILVER";
        else if (point >= 13 && point <= 16)
            return "GOLD";
        else if (point >= 17 && point <= 20)
            return "PLATINUM";
        else if (point >= 21 && point <= 24)
            return "DIAMOND";
        else if (point >= 25 && point <= 35)
            return "MASTER";
        else if (point >= 36 && point <= 45)
            return "GRANDMASTER";
        else
            return "CHALLNEGER";
    }

    public int  getAvgTier(String summonerId, JSONObject player) throws org.json.simple.parser.ParseException {
        ResponseEntity<String> response = getResponseEntityByEncryptedUserId(summonerId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray)obj;
        JSONObject jsonObj = (JSONObject)jsonArray.get(0);
        String tier = (String)jsonObj.get("tier");
        String rank = (String)jsonObj.get("rank");
    //    if (tier == "MASTER" || tier == "GRANDMASTER" || tier == "CHALLENGER")
        if (tier.equals("MASTER"))
            player.put("tier", tier);
        else
            player.put("tier", tier + rank);
        return TierToInt(tier, rank);
    }

    public String playTimeFormatting(int second){
        int h = second / 3600;
        int m = (second - (h * 3600)) / 60;
        int s = second % 60;
        String ret = null;
        if (h != 0) {
            ret = String.valueOf(h) + "시" + String.valueOf(m) + "분" + String.valueOf(s) + "초";
        }
        else{
            ret = String.valueOf(m) + "분" + String.valueOf(s) + "초";
        }
        return ret;
    }

    public String lastPlayTimeFormatting(int second){
        int month = second / (30 * 86400);
        int week = (second - (month * 30 * 86400)) / (7 * 86400);
        int date = ((second - (month * 30 * 86400)) - week * 7 * 86400) / 86400;
        int h = (second % 86400) / 3600;
        int m = ((second % (86400)) % 3600) % 60;
        String ret;
        System.out.println(" month = " + month);
        System.out.println("second = " + second);
        if (month != 0)
            ret = String.valueOf(month) + "달 전";
        else if (week != 0)
            ret = String.valueOf(week) + "주 전";
        else if (date != 0)
            ret = String.valueOf(date) + "일 전";
        else if (h != 0)
            ret = String.valueOf(h) + "시간 전";
        else
            ret = String.valueOf(m) + "분 전";
        return ret;
    }

    public int matchPlayTime(JSONObject info, JSONObject userRecord){
        int playTime = ParseToInt(info, "gameDuration");
        long playBefore = Instant.now().getEpochSecond() - Math.round((Long.parseLong(String.valueOf(info.get("gameEndTimestamp"))) / 1000));
        userRecord.put("playtime", playTimeFormatting(playTime));
        userRecord.put("lastPlayTime", lastPlayTimeFormatting((int)playBefore));
        return playTime;
    }

    public void matchCsAndWard(JSONObject user, JSONObject userRecord, int playTime){
        int cs = ParseToInt(user, "neutralMinionsKilled") + ParseToInt(user, "totalMinionsKilled");
        double csPerMinutes = (double)cs / ((double)playTime / 60);
        int visionWard = ParseToInt(user, "visionWardsBoughtInGame");
        userRecord.put("cs", String.valueOf(cs));
        userRecord.put("csPerMiutes", String.valueOf(csPerMinutes));
        userRecord.put("visionWard", String.valueOf(visionWard));
    }

    public void getKillRate(JSONObject info, JSONObject user ,JSONObject userRecord){
        int kill = ParseToInt(user, "kills");
        int userTeamId = ParseToInt(user ,"teamId");
        int totalKill = 0;
        int killRate;

        JSONArray team = (JSONArray)info.get("teams");
        System.out.println(team.getClass().getName());
        JSONObject teamToObj = (JSONObject)team.get(0);
        JSONObject objectives = (JSONObject) teamToObj.get("objectives");
        JSONObject objectChamp;
        if (userTeamId == 100){
            objectChamp = (JSONObject) objectives.get("champion");
            totalKill = ParseToInt(objectChamp ,"kills");
        }
        else{
            teamToObj = (JSONObject)team.get(1);
            objectives = (JSONObject) teamToObj.get("objectives");
            objectChamp = (JSONObject) objectives.get("champion");
            totalKill = totalKill = ParseToInt(objectChamp ,"kills");
        }
        killRate = (int)(Math.round((double) kill / (double) totalKill * 100));
        userRecord.put("killRate", String.valueOf(killRate)+"%"); // 킬관여 세팅
    }

    public void matchKdaAndWinRecord(JSONArray participants, JSONObject user, JSONObject userRecord) {

        int kill = ParseToInt(user, "kills");
        int death = ParseToInt(user, "deaths");
        int assist = ParseToInt(user, "assists");
        userRecord.put("kill", Integer.toString(kill));
        userRecord.put("death", Integer.toString(death));
        userRecord.put("assist", Integer.toString(assist));
        System.out.println("assist = " + assist);
        double kda = 0;
        if (death == 0)
            userRecord.put("kda", "perfect");
        else {
            kda = ((double)kill + (double)assist / (double)death);
            userRecord.put("kda", Double.toString(kda));
        }
        // -> k/d/a 및 kda 세팅
        if (user.get("win") == TRUE)
            userRecord.put("win", TRUE);
        else
            userRecord.put("win", FALSE); // 승리여부
    }

    public void matchRuneAndSpellSet(JSONObject user, JSONObject userRecord){
        JSONObject perks = (JSONObject) user.get("perks");
        JSONArray styles = (JSONArray)perks.get("styles");
        JSONObject stylesObj = (JSONObject) styles.get(0);
        JSONArray selections = (JSONArray) stylesObj.get("selections");
        JSONObject primary = (JSONObject)selections.get(0);
        userRecord.put("primaryRune", primary.get("perk"));
        JSONObject semi = (JSONObject)styles.get(1);
        userRecord.put("semiRune", semi.get("style"));
        // -> 룬 셋팅 완료
        userRecord.put("spell1", user.get("summoner1Casts"));
        userRecord.put("spell2", user.get("summoner2Casts"));
    }

    public void setKdaWinRate(JSONObject user, GameTwentyRecord twentyRecord){

        int kill = ParseToInt(user, "kills");
        int death = ParseToInt(user, "deaths");
        int assist = ParseToInt(user, "assists");

        int win = 0;
        int lose = 1;
        int draw = 0;

        twentyRecord.setKill(twentyRecord.getKill() + kill);
        twentyRecord.setDeath(twentyRecord.getDeath() + death);
        twentyRecord.setAssist(twentyRecord.getAssist() + assist);
        if (user.get("win") == TRUE){
            win = 1;
            lose = 0;
        }
        if (user.get("teamEarlySurrendered") == TRUE)
            draw = 1;
        twentyRecord.setWin((twentyRecord.getWin()) + win);
        twentyRecord.setLose((twentyRecord.getLose()) + lose);
        twentyRecord.setDraw((twentyRecord.getDraw()) + draw);
        return ;
    }

    public JSONObject setKdaWinRateDto(GameTwentyRecord gameTwentyRecord, JSONObject json){
        json.put("kill", Integer.toString(gameTwentyRecord.getKill()));
        json.put("death", Integer.toString(gameTwentyRecord.getDeath()));
        json.put("assist", Integer.toString(gameTwentyRecord.getAssist()));
        json.put("win", Integer.toString(gameTwentyRecord.getWin()));
        json.put("lose", Integer.toString(gameTwentyRecord.getLose()));
        json.put("draw", Integer.toString(gameTwentyRecord.getDraw()));
        json.put("winRate", Double.toString(gameTwentyRecord.getCalculatedWinRate()));
        json.put("kda", Double.toString(gameTwentyRecord.getCalculatedKda()));
        return json;
    }

// participants 에서 전적 검색에 해당되는 유저의 jsonObject 를 갖고 옵니다.
    public JSONObject getUserFromJson(JSONArray participants, String lolName){
        JSONObject user = null;
        String name = null;
        for (int i = 0; i < 9; i++){
            user = (JSONObject) participants.get(i);
            name = (String)user.get("summonerName");
            if (name.equals(lolName));
                break ;
        }
        return user;
    }

    public String getUserPid(String lolName) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;
        
        String userPid = (String)jsonObj.get("puuid");
        System.out.println("userPid = " + userPid);
        return userPid;
    }

    public ArrayList getMatchId(String pid) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getResponseEntityByUserPid(pid);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray) obj;

        ArrayList matchList = new ArrayList<>(20);
        for (int i = 0; i < 20; i++){
            matchList.add(jsonArray.get(i));
        }
        return matchList;
    }

    public int ParseToInt(JSONObject obj, String str){
        int  retNum = 0;
        retNum = Integer.parseInt(String.valueOf(obj.get(str)));
        return retNum;
    }

    private ResponseEntity<String> getResponseEntityByUserName(String userName){
        String url="https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
        url+=userName;
        url+="?api_key=";
        url += ApiKey;

        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", UserAgent);
        headers.set("Accept-Language", AcceptLanguage);
        headers.set("Accept-Charset",AcceptCharset);
        headers.set("Origin", Origin);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        return response;
    }

    private ResponseEntity<String> getResponseEntityByUserPid(String userPid){
        String url="https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/";
        url+=userPid;
        url+="/ids?start=0&count=20";
        url+="&api_key=";
        url += ApiKey;

        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", UserAgent);
        headers.set("Accept-Language", AcceptLanguage);
        headers.set("Accept-Charset",AcceptCharset);
        headers.set("Origin", Origin);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        return response;
    }
/*
    private ResponseEntity<String> getResponseEntityByMatchId(String matchId){
        String url="https://asia.api.riotgames.com/lol/match/v5/matches/";
        url+=matchId;
        url+="?api_key=";
        url += ApiKey;

        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", UserAgent);
        headers.set("Accept-Language", AcceptLanguage);
        headers.set("Accept-Charset",AcceptCharset);
        headers.set("Origin", Origin);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        return response;
    }
*/
    private ResponseEntity<String> getResponseEntityByEncryptedUserId(String userID){
        String url="https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";
        url+=userID;
        url+="?api_key=";
        url += ApiKey;

        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", UserAgent);
        headers.set("Accept-Language", AcceptLanguage);
        headers.set("Accept-Charset",AcceptCharset);
        headers.set("Origin", Origin);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        return response;
    }
}
