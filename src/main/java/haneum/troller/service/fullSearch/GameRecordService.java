package haneum.troller.service.fullSearch;


import haneum.troller.common.apiKey.LolApiKey;
import haneum.troller.dto.gameRecord.GameRecordDto;
import haneum.troller.service.dataDragon.ChampionImgService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.time.Instant;
import java.util.Iterator;

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

    public GameRecordDto getGameRecord(String lolName) throws ParseException, org.json.simple.parser.ParseException, IOException {

        GameRecordDto gameRecordDto = new GameRecordDto();
        JSONArray gameRecordArray = new JSONArray();
        JSONObject gameTwentyRecordObject = new JSONObject();
        GameTwentyRecord gameTwentyRecord = new GameTwentyRecord();
        GameMostChampionRecord gameMostChampionRecord;
        gameMostChampionRecord = new GameMostChampionRecord();
        ArrayList<Class>champion = new ArrayList<>(20);

//        JSONObject spellFile = parsingJsonFIle(getPath("Spell"));
//        JSONArray runeFile = parsingJsonFIleArray(getPath("Rune"));
        JSONArray runeFile = readJsonArrayFromUrl("https://ddragon.leagueoflegends.com/cdn/12.15.1/data/ko_KR/runesReforged.json");
        JSONObject spellFile = readJsonObjFromUrl("https://ddragon.leagueoflegends.com/cdn/12.15.1/data/ko_KR/summoner.json");
        JSONObject itemFile = readJsonObjFromUrl("https://ddragon.leagueoflegends.com/cdn/12.15.1/data/ko_KR/item.json");
        String userPid = getUserPid(lolName);
        ArrayList matchList = getMatchId(userPid);
        for (int i = 0; i < 20; i++){
            gameRecordArray.add(setGameRecord((String) matchList.get(i), gameTwentyRecord, runeFile,
                    spellFile, itemFile,lolName));
        }
        gameRecordDto.setLatestTwentyRecords(setKdaWinRateDto(gameTwentyRecord, gameTwentyRecordObject));
        gameRecordDto.setGameRecord(gameRecordArray);
        return gameRecordDto;
    }

    public JSONObject setGameRecord(String matchId , GameTwentyRecord twentyRecord, JSONArray rune, JSONObject spell,
                                    JSONObject item ,String lolName) throws org.json.simple.parser.ParseException, IOException {
        String response = getResponseEntityByMatchId(matchId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response);
        JSONObject jsonObj = (JSONObject)obj;
        JSONObject info = (JSONObject)jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject userRecord = new JSONObject();
        JSONObject user = getUserFromJson(participants, lolName);
        userRecord.put("gameMode", (String)info.get("gameMode"));
        String championName = (String)user.get("championName");
        userRecord.put("championName", championName);
        userRecord.put("championUI", championImgService.getChampionImg(championName));
        setKdaWinRate(user, twentyRecord);
        matchKdaAndWinRecord(participants, user, userRecord);
        matchMetaDataSet(user, userRecord, rune, spell, item);
        getKillRate(info, user, userRecord);
        int playTime = matchPlayTime(info, userRecord);
        matchCsAndWard(user, userRecord, playTime);
        getAvgTier(lolName, userRecord);
        JSONArray players = setPlayers(user, participants); //10명의 사용자 정보 링크와 평균 티어만 구하는 함수.
        userRecord.put("players", players);
        return userRecord;
    }

    public JSONArray setPlayers(JSONObject user,JSONArray participants) throws org.json.simple.parser.ParseException {
        double avgTier = 0; // save average tier of players
        JSONArray players = new JSONArray();
        for (int i = 0; i < 10; i++){
            getPlayers((JSONObject)participants.get(i), players, i); // get player's info
        }
        user.put("Tier", IntToTier((int)avgTier));
        return players;
    }

    public void getPlayers(JSONObject participant, JSONArray players, int teamIndex) throws org.json.simple.parser.ParseException {
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
        players.add(player);
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

    public void  getAvgTier(String lolName, JSONObject userRecord) throws org.json.simple.parser.ParseException {
        String summonerId = getEncryptedId(lolName);
        ResponseEntity<String> response = getResponseEntityByEncryptedUserId(summonerId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray)obj;
        JSONObject jsonObj;
        jsonObj = (JSONObject)jsonArray.get(0);
        String cmpGame = (String)jsonObj.get("queueType");
        if (cmpGame.equals("RANKED_SOLO_5x5") == FALSE)
            jsonObj = (JSONObject)jsonArray.get(1);
        String tier = (String)jsonObj.get("tier");
        String rank = (String)jsonObj.get("rank");
        if (tier.equals("MASTER") || tier.equals("GRANDMASTER") || tier.equals("CAHLLENGER"))
            userRecord.put("averageTier", tier);
        else
            userRecord.put("averageTier", tier + rank);
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
        double csPerMinutes = (double)cs / ((double)playTime / 60) * 10;
        csPerMinutes = Math.round(csPerMinutes);
        csPerMinutes /= 10;
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
        double kda = 0;
        if (death == 0)
            userRecord.put("kda", "perfect");
        else {
            kda = ((double)kill + (double)assist / (double)death) * 100;
            kda = Math.round(kda);
            kda = kda / 100;
            userRecord.put("kda", Double.toString(kda));
        }
        // -> k/d/a 및 kda 세팅
        if (user.get("win") == TRUE)
            userRecord.put("win", TRUE);
        else
            userRecord.put("win", FALSE); // 승리여부
    }

    public void matchMetaDataSet(JSONObject user, JSONObject userRecord, JSONArray rune, JSONObject spell, JSONObject item) throws IOException, org.json.simple.parser.ParseException {

        String primaryRune = null;
        String primaryRuneImg = null;
        JSONObject perks = (JSONObject) user.get("perks");
        JSONArray styles = (JSONArray)perks.get("styles");
        JSONObject stylesObj = (JSONObject) styles.get(0);
        JSONArray selections = (JSONArray) stylesObj.get("selections");
        JSONObject primary = (JSONObject)selections.get(0);
        JSONObject semi = (JSONObject)styles.get(1);
        int primaryRuneNum = ParseToInt(primary ,"perk");
        int semiRuneNum = ParseToInt(semi, "style");
        if (primaryRuneNum == 9923) {
            primaryRune = "칼날비";
            primaryRuneImg = "https://ddragon.canisback.com/img/perk-images/Styles/Domination/HailOfBlades/HailOfBlades.png";
        }
        else{
            primaryRune = getRuneInfo(rune, primaryRuneNum, "name");
            primaryRuneImg = getRuneInfo(rune, primaryRuneNum, "icon");
        }
        String semiRune = getSemiRuneInfo(rune, semiRuneNum, "name");
        String semiRuneImg = getSemiRuneInfo(rune, semiRuneNum, "icon");
        userRecord.put("primaryRune",primaryRune);
        userRecord.put("primaryRuneImg", primaryRuneImg);
        userRecord.put("semiRune", semiRune);
        userRecord.put("semiRuneImg", semiRuneImg);
        // -> 룬 셋팅 완료
        setSpellInfo(userRecord, ParseToInt(user, "summoner1Id"), "spell1");
        setSpellInfo(userRecord, ParseToInt(user, "summoner2Id"), "spell2");
        // -> 스펠 셋팅 완료
        JSONArray itemArray = new JSONArray();
        for (int i = 0; i < 7; i++){
            setItemInfo(item, itemArray, user, i);
        }
        userRecord.put("itemArray" ,itemArray);
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
        double avgKill = getAvgKda(gameTwentyRecord.getKill());
        double avgAssist = getAvgKda(gameTwentyRecord.getAssist());
        double avgDeath = getAvgKda(gameTwentyRecord.getDeath());
        String kdaRound = Double.toString(gameTwentyRecord.getCalculatedKda());
        if (kdaRound.length() >= 5)
            kdaRound = kdaRound.substring(0, 4);
        String winRound = Integer.toString((int)(gameTwentyRecord.getCalculatedWinRate() * 100));
        json.put("averageKill", Double.toString(avgKill));
        json.put("averageDeath", Double.toString(avgDeath));
        json.put("averageAssist", Double.toString(avgAssist));
        json.put("win", Integer.toString(gameTwentyRecord.getWin()));
        json.put("lose", Integer.toString(gameTwentyRecord.getLose()));
        json.put("draw", Integer.toString(gameTwentyRecord.getDraw()));
        json.put("winRate", winRound + "%");
        json.put("averageKda",  kdaRound);
        return json;
    }

    // participants 에서 전적 검색에 해당되는 유저의 jsonObject 를 갖고 옵니다.
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

    public String getEncryptedId(String lolName) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;
        String summonerId = (String)jsonObj.get("id");
        return summonerId;
    }

    public String getUserPid(String lolName) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;

        String userPid = (String)jsonObj.get("puuid");
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

    public Double getAvgKda(int killOrDeathOrAssist){

        if (killOrDeathOrAssist == 0)
            return (double)0;
        double n = ((double)killOrDeathOrAssist / 20) * 10;
        n = Math.round(n);
        double avg = n / 10 ;
        return avg;
    }

    public int ParseToInt(JSONObject obj, String str){
        int  retNum = 0;
        retNum = Integer.parseInt(String.valueOf(obj.get(str)));
        return retNum;
    }

//    public JSONObject parsingJsonFIle(String realPath) throws IOException, org.json.simple.parser.ParseException {
//        FileReader reader = new FileReader(realPath);
//        JSONParser parser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) parser.parse(reader);
//        return jsonObject;
//    }
//
//    public JSONArray parsingJsonFIleArray(String realPath) throws IOException, org.json.simple.parser.ParseException {
//        FileReader reader = new FileReader(realPath);
//        JSONParser parser = new JSONParser();
//        JSONArray jsonA = (JSONArray) parser.parse(reader);
//        return jsonA;
//    }
//
//    public String getPath(String path){
//        return "src/main/resources/RuneMetaData/" + path + ".json";
//    }

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

    private String getResponseEntityByMatchId(String matchId) {
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/";
        url += matchId;
        url += "?api_key=";
        url += ApiKey;

        HttpResponse response;
        String entity;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);

            response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            entity = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return entity;
    }

    public String getRuneInfo(JSONArray runeFile, int runeNum, String flag){
        JSONObject runePage = selectRune(runeNum, runeFile);
        JSONArray slots = (JSONArray) runePage.get("slots");
        JSONObject rune = (JSONObject) slots.get(0);
        JSONArray runes = (JSONArray)rune.get("runes");
        return searchRuneInfo(runes, runeNum, flag);
    }

    public String getSemiRuneInfo(JSONArray runeFile, int runeNum, String flag){
        JSONObject semiRunePage = selectRune(runeNum, runeFile);
        if (flag == "icon")
            return "https://ddragon.canisback.com/img/" + (String) semiRunePage.get(flag);
        return (String) semiRunePage.get(flag);
    }

    public String searchRuneInfo(JSONArray runes , int runeNum, String flag){
        for (int i = 0; i < runes.size(); i++){
            JSONObject rune = (JSONObject) runes.get(i);
            if (runeNum == ParseToInt(rune, "id")){
                if (flag == "icon")
                    return "https://ddragon.canisback.com/img/" + (String) rune.get(flag);
                return (String) rune.get(flag);
            }
        }
        return null;

    }

    public JSONObject selectRune(int runeNum, JSONArray runeFile){
        JSONObject runePage = null;
        int div = runeNum - (runeNum % 100);
        if (div == 8100)
            runePage = (JSONObject) runeFile.get(0);
        else if (div == 8300)
            runePage = (JSONObject) runeFile.get(1);
        else if (div == 8000)
            runePage = (JSONObject) runeFile.get(2);
        else if (div == 8400)
            runePage = (JSONObject) runeFile.get(3);
        else if (div == 8200)
            runePage = (JSONObject) runeFile.get(4);
        return runePage;
    }

    public void setSpellInfo(JSONObject userRecord ,int spellNum, String flag){
        switch (spellNum){
            case 21:
                userRecord.put(flag, "방어막");
                userRecord.put(flag + "img", setSpellImg("SummonerBarrier"));
                break;
            case 1:
                userRecord.put(flag, "정화");
                userRecord.put(flag + "img", setSpellImg("SummonerBoost"));
                break;
            case 14:
                userRecord.put(flag, "점화");
                userRecord.put(flag + "img", setSpellImg("SummonerDot"));
                break;
            case 3:
                userRecord.put(flag, "탈진");
                userRecord.put(flag + "img", setSpellImg("SummonerExhaust"));
                break;
            case 4:
                userRecord.put(flag, "점멸");
                userRecord.put(flag + "img", setSpellImg("SummonerFlash"));
                break;
            case 6:
                userRecord.put(flag, "유체화");
                userRecord.put(flag + "img", setSpellImg("SummonerHaste"));
                break;
            case 7:
                userRecord.put(flag, "회복");
                userRecord.put(flag + "img", setSpellImg("SummonerHeal"));
                break;
            case 13:
                userRecord.put(flag, "총명");
                userRecord.put(flag + "img", setSpellImg("SummonerMana"));
                break;
            case 31:
                userRecord.put(flag, "포로 던지기");
                userRecord.put(flag + "img", setSpellImg("SummonerPoroThrow"));
                break;
            case 11:
                userRecord.put(flag, "강타");
                userRecord.put(flag + "img", setSpellImg("SummonerSmate"));
                break;
            case 32:
                userRecord.put(flag, "표식");
                userRecord.put(flag + "img", setSpellImg("SummonerSnowball"));
                break;
            case 12:
                userRecord.put(flag, "텔레포트");
                userRecord.put(flag + "img", setSpellImg("SummonerTeleport"));
                break;
        }
    }

    public String setSpellImg(String img){
        return "http://ddragon.leagueoflegends.com/cdn/10.3.1/img/spell/" + img + ".png";
    }
    public String setItemImg(String img){
        return "http://ddragon.leagueoflegends.com/cdn/10.3.1/img/item/" + img + ".png";
    }

    public String Read(Reader re) throws io.jsonwebtoken.io.IOException, java.io.IOException {     // class Declaration
        StringBuilder str = new StringBuilder();     // To Store Url Data In String.
        int temp;
        do {

            temp = re.read();
            str.append((char) temp);

        } while (temp != -1);
        //  re.read() return -1 when there is end of buffer , data or end of file.

        return str.toString();
    }

    public JSONObject readJsonFromUrlMethod(String link) throws io.jsonwebtoken.io.IOException, java.io.IOException, org.json.simple.parser.ParseException {
        InputStream input = new URL(link).openStream();
        BufferedReader re = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        // Buffer Reading In UTF-8
        String text = Read(re);
        text = text.substring(0, text.length() - 1);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new StringReader(text));
        JSONObject jsonObj = (JSONObject) obj;
        input.close();
        return jsonObj;    // Returning JSON
    }

    public JSONArray readJsonArrayFromUrlMethod(String link)throws io.jsonwebtoken.io.IOException, java.io.IOException, org.json.simple.parser.ParseException {
        InputStream input = new URL(link).openStream();
        BufferedReader re = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        // Buffer Reading In UTF-8
        String text = Read(re);
        text = text.substring(0, text.length() - 1);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new StringReader(text));
        JSONArray jsonArray = (JSONArray) obj;
        input.close();
        return jsonArray;    // Returning JSON
    }

    public JSONObject readJsonObjFromUrl(String url) throws IOException, org.json.simple.parser.ParseException {
        return readJsonFromUrlMethod(url);  // calling method in order to read.
    }

    public JSONArray readJsonArrayFromUrl(String url) throws IOException, org.json.simple.parser.ParseException {
        return readJsonArrayFromUrlMethod(url);
    }

    public void setItemInfo(JSONObject item, JSONArray itemArray, JSONObject user, int i){

        JSONObject data = (JSONObject) item.get("data");
        JSONObject itemInfo = new JSONObject();
        int itemInt = ParseToInt(user, "item" + i);
        if (itemInt == 0){
            itemInfo.put("item" + i, "None");
            itemInfo.put("itemImg" + i, "None");
            itemArray.add(itemInfo);
            return ;
        }
        String dataStr = Integer.toString(ParseToInt(user, "item" + i));
        JSONObject itemData = (JSONObject) data.get(dataStr);
        String itemNameStr = (String)itemData.get("name");
        itemInfo.put("item" + i, itemNameStr);
        itemInfo.put("item" + i + "Img", setItemImg(dataStr));
        itemArray.add(itemInfo);
    }
}
