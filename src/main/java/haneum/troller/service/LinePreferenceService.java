package haneum.troller.service;

import haneum.troller.common.config.apiKey.LolApiKey;
import haneum.troller.dto.linePrefer.LinePreferenceDto;
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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class LinePreferenceService {
    @Autowired
    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    private static final String ApiKey= LolApiKey.API_KEY;
    private String summonerName;
    public GameRecordService gameRecordService;


    public LinePreferenceDto getLinePreferenceDto(String lolName) throws ParseException, IOException {

        LinePreferenceDto linePreferenceDto = new LinePreferenceDto();
        ArrayList<String> position = new ArrayList<>(20);
        String userPid = null;
    /*    try {
                 userPid = getUserPid(lolName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList matchList = getMatchId(userPid); */
        String test = "test";
        for (int i = 1; i < 21; i++){ // 원래는 i = 0 i < 20
        //    settingLinePreference((String)matchList.get(i), position);
            settingLinePreference(i, position);
        }
        linePreferenceDto.setFirstLinePreference((String)matchLinePreference(position).get("firstLinePreference"));
        linePreferenceDto.setFirstLinePlayed((String)matchLinePreference(position).get("firstLinePlayed"));
        linePreferenceDto.setSecondLinePreference((String)matchLinePreference(position).get("secondLinePreference"));
        linePreferenceDto.setSecondLinePlayed((String)matchLinePreference(position).get("secondLinePlayed"));
        return linePreferenceDto;
    }
    // 원래 i 대신에 String matchId 가 와야 함
    public void settingLinePreference(int i, ArrayList position) throws ParseException, IOException {
    //    ResponseEntity<String>response = getResponseEntityByMatchId(matchId);

        FileReader reader = new FileReader("/Users/ojeongmin/Documents/lol_json/test" + Integer.toString(i) + ".json");
        JSONParser parser = new JSONParser();
   //     Object obj = parser.parse(response.getBody());
        Object obj = parser.parse(reader);
        JSONObject jsonObj = (JSONObject)obj;
        JSONObject info = (JSONObject)jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject user = getUserFromJson(participants);
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
                System.out.println("key = " + key);
                linePreference.put("secondLinePreference", key);
                linePreference.put("secondLinePlayed", String.valueOf(winRate));
            }
            i++;
            if (i == 2)
                break;
        }
        return linePreference;
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

    public JSONObject getUserFromJson(JSONArray participants){
        String userName = getSummonerName();
        JSONObject user = null;
        for (int i = 0; i < 9; i++){
            user = (JSONObject) participants.get(i);
            if (user.get("summonerName") == userName);
            break ;
        }
        return user;
    }

    private String getSummonerName() {
        return summonerName;
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

    private ResponseEntity<String> getResponseEntityByMatchId(String matchId){
        String url="https://asia.api.riotgames.com/lol/match/v5/matches/";
        url+=matchId;
        url+="&api_key=";
        url += "RGAPI-7ae02462-bd19-4490-8f9d-c730004e0bd3";

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

/*    private ResponseEntity<String> getResponseEntityByMatchId(String matchId) {
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

        ResponseEntity response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );
        return response;
    } */
}

