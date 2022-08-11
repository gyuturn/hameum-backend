package haneum.troller.service;

import haneum.troller.common.config.apiKey.LolApiKey;
import haneum.troller.dto.mostChampion.MostThreeChampionDto;
import haneum.troller.service.dataDragon.ChampionImgService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.io.FileReader;

import static java.lang.Boolean.TRUE;

@Service
public class MostThreeChampionService {

    @Autowired
    public ChampionImgService championImgService;

    private static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage = "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin = "https://developer.riotgames.com";
    private static final String ApiKey = LolApiKey.API_KEY;

    public MostThreeChampionDto getMostThreeChampionDto(String lolName) throws ParseException, IOException {
        MostThreeChampionDto mostThreeChampionDto = new MostThreeChampionDto();
        ArrayList<HashMap>champions = new ArrayList<>();
        String userPid = null;
        try {
            userPid = getUserPid(lolName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList matchList = getMatchId(userPid);
        int cnt = 0;
        for (int i = 0; i < 20; i++){
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
        String response = getResponseEntityByMatchId(matchId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response);
    //    FileReader reader = new FileReader("/Users/ojeongmin/Documents/lol_json/test" + Integer.toString(i) + ".json");
    //    Object obj = parser.parse(reader);
        JSONObject jsonObj = (JSONObject)obj;
        JSONObject info = (JSONObject)jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject user = getUserFromJson(participants, lolName);
        String className = (String)user.get("championName");
        int playTime = ParseToInt(info, "gameDuration");
        int index;
        GameMostChampionRecord gameMostChampionRecord = null;
        if (isContain(champions, className)) {
            index = getIndexOf(champions, className);
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
        setKdaWinRate(user, gameMostChampionRecord);
        gameMostChampionRecord.setChampionName(className);
        setCs(user, gameMostChampionRecord, playTime);
    }

    public void setKdaWinRate(JSONObject user,  GameMostChampionRecord gameMostChampionRecord){

        int kill = ParseToInt(user ,"kills");
        int death = ParseToInt(user ,"deaths");
        int assist = ParseToInt(user, "assists");
        int win = 0;
        int draw = 0;
        int lose = 0;

        gameMostChampionRecord.setKill(gameMostChampionRecord.getKill() + kill);
        gameMostChampionRecord.setDeath(gameMostChampionRecord.getDeath() + death);
        gameMostChampionRecord.setAssist(gameMostChampionRecord.getAssist() + assist);
        if (user.get("win") == TRUE)
            win = 1;
        else
            lose = 1;
        if (user.get("teamEarlySurrendered") == TRUE)
            draw = 1;
        gameMostChampionRecord.setWin((gameMostChampionRecord.getWin()) + win);
        gameMostChampionRecord.setLose(gameMostChampionRecord.getLose() + lose);
        gameMostChampionRecord.setDraw((gameMostChampionRecord.getDraw()) + draw);
        return ;
    }

    public void setCs(JSONObject user, GameMostChampionRecord gameMostChampionRecord, int playTime){
        int cs = ParseToInt(user ,"neutralMinionsKilled") + ParseToInt(user, "totalMinionsKilled");
        double csPerMinutes = Math.round((double)cs / ((double)playTime / 60) * 10) / 10.0;
        gameMostChampionRecord.setCs(cs);
        gameMostChampionRecord.setCsPerMinutes(csPerMinutes);
    }

    public boolean isContain(ArrayList champions, String className){
      GameMostChampionRecord game = null;
      String compare = null;
      for(int i = 0; i < champions.size(); i++){
          game = (GameMostChampionRecord) champions.get(i);
          compare = game.getChampionName();
          if (compare.compareTo(className) == 0) {
              return true;
          }
      }
      return false;
    }

    public int  getIndexOf(ArrayList champions, String className){
        GameMostChampionRecord game = null;
        String compare = null;
        for(int i = 0; i < champions.size(); i++){
            game = (GameMostChampionRecord) champions.get(i);
            compare = game.getChampionName();
            if (compare.compareTo(className) == 0) {
                return i;
            }
        }
        return -1;
    }

    public JSONObject getUserFromJson(JSONArray participants, String lolName){
        JSONObject user = null;
        String name = null;
        for (int i = 0; i < 10; i++){
            user = (JSONObject) participants.get(i);
            name = (String)user.get("summonerName");
            if (name.equals(lolName)) {
                break;
            }
        }
        return user;
    }

    public int ParseToInt(JSONObject obj, String str){
        int  retNum = 0;
        retNum = Integer.parseInt(String.valueOf(obj.get(str)));
        return retNum;
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

    public String getUserPid(String lolName) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;

        String userPid = (String)jsonObj.get("puuid");
        return userPid;
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

    private ResponseEntity<String> getResponseEntityByEncryptedUserId(String userID) {
        String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";
        url += userID;
        url += "?api_key=";
        url += ApiKey;

        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", UserAgent);
        headers.set("Accept-Language", AcceptLanguage);
        headers.set("Accept-Charset", AcceptCharset);
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

    private ResponseEntity<String> getResponseEntityByUserName(String userName) {
        String url = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
        url += userName;
        url += "?api_key=";
        url += ApiKey;

        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", UserAgent);
        headers.set("Accept-Language", AcceptLanguage);
        headers.set("Accept-Charset", AcceptCharset);
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
}