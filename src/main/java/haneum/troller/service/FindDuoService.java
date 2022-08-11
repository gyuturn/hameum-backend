package haneum.troller.service;

import haneum.troller.common.config.apiKey.LolApiKey;
import haneum.troller.dto.findDuo.FindDuoDto;
import haneum.troller.service.dataDragon.ChampionImgService;
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
import static org.apache.coyote.http11.Constants.a;
@Service
public class FindDuoService {
    @Autowired
    private ChampionImgService championImgService;

    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    private static final String ApiKey= LolApiKey.API_KEY;
    private String summonerName;

    public FindDuoDto getFindDuoDto(String lolName) throws ParseException {
        FindDuoDto findDuoDto = new FindDuoDto();
        String userPid = getUserPid(lolName);
        ArrayList matchList = getMatchId(userPid);
        ArrayList champions = new ArrayList<>();
        ArrayList retArray = new ArrayList();
        ArrayList lines = new ArrayList();
        GameTwentyRecord gameTwentyRecord = new GameTwentyRecord();
        setTierPoint(lolName, findDuoDto);
        for (int i = 0; i < 20; i++){
            setGameRecord((String)matchList.get(i), gameTwentyRecord, lines, champions);
        }
        setKdaWinRateDto(gameTwentyRecord, findDuoDto);
        setMostThree(champions, findDuoDto); // most three champ;
        findDuoDto.setLolName(lolName);
        findDuoDto.setFavorPositionDesc(matchLinePreference(lines));
        return findDuoDto;
    }

    public void setGameRecord(String matchId,GameTwentyRecord twentyRecord, ArrayList lines ,ArrayList champions) throws org.json.simple.parser.ParseException {
        ResponseEntity<String> response = getResponseEntityByMatchId(matchId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject) obj;
        JSONObject info = (JSONObject) jsonObj.get("info");
        JSONArray participants = (JSONArray) info.get("participants");
        JSONObject user = getUserFromJson(participants);
        setKdaWinRate(user, twentyRecord);
        champions.add(user.get("championName")); // 20판에서 챔피언 기록.
        lines.add(user.get("teamPosition")); // 20판에서 라인 기록.
    }

    public void setMostThree(ArrayList champions, FindDuoDto findDuoDto)
    {
        String champion = null;
        ArrayList<String> championArray = new ArrayList<>(3);
        HashMap<String, Integer>map = new HashMap<>();
        for (int i = 0; i < 20; i++){
            champion = (String)champions.get(i);
            if (map.containsKey(champion) == TRUE)
                map.replace(champion, map.get(champion) + 1);
            else
                map.put(champion, 1);
        }
        List<String> keySet = new ArrayList<>(map.keySet());
        keySet.sort((o1, o2) -> (int) (map.get(o2) - map.get(o1)));
        int i = 0;
        for (String key : keySet){
            championArray.add(championImgService.getChampionImg(String.valueOf(map.get(key))));
            i++;
            if (i == 3)
                break;
        }
        findDuoDto.setMostChampion(championArray);
    }

    public String matchLinePreference(ArrayList position){
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
        String line = null;
        for (String key : keySet) {
            if (i == 0) {
                line = String.valueOf(map.get(key));
                if (line == "TOP")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-top-blue.png";
                else if (line == "JUNGLE")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-jungle-blue.png";
                else if (line == "MID")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-mid-blue.png";
                else if (line == "BOTTOM")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-bottom-blue.png";
                else
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-utility-blue.png";
                break;
                }
        }
        return line;
    }

    public void setKdaWinRate(JSONObject user, GameTwentyRecord twentyRecord){

        int kill = (int)user.get("kills");
        int death = (int)user.get("deaths");
        int assist = (int)user.get("assists");

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

    public void setKdaWinRateDto(GameTwentyRecord gameTwentyRecord, FindDuoDto dto){
        dto.setKill(Integer.toString(gameTwentyRecord.getKill()));
        dto.setDeath(Integer.toString(gameTwentyRecord.getDeath()));
        dto.setAssist(Integer.toString(gameTwentyRecord.getAssist()));
        dto.setWin(Integer.toString(gameTwentyRecord.getWin()));
        dto.setLose(Integer.toString(gameTwentyRecord.getLose()));
        dto.setWinRate(Double.toString(gameTwentyRecord.getWinRate()));
    }

    public void setTierPoint(String lolName, FindDuoDto findDuoDto) throws ParseException {
        ResponseEntity<String> response = getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;
        String encryptedId = (String)jsonObj.get("id");

        ResponseEntity<String> response2 = getResponseEntityByEncryptedUserId(encryptedId);
        obj = parser.parse(response2.getBody());
        JSONArray jsonArray = (JSONArray)obj;
        jsonObj = (JSONObject)jsonArray.get(0);

        findDuoDto.setTier(jsonObj.get("tier").toString());
        findDuoDto.setLeaguePoint(jsonObj.get("leaguePoints").toString());
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

    public String getSummonerName(){
        return summonerName;
    }

    public String getUserPid(String lolName) throws org.json.simple.parser.ParseException {
        ResponseEntity<String> response = getResponseEntityByUserName(lolName);

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
}
