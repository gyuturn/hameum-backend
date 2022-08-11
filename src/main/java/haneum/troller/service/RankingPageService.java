package haneum.troller.service;

import haneum.troller.common.apiKey.LolApiKey;
import haneum.troller.dto.ranking.RankingPageDto;
import haneum.troller.service.dataDragon.MyPageImgService;
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

import java.util.*;
import java.util.List;

@Service
public class RankingPageService {

    @Autowired
    private MyPageImgService myPageImgService;

    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    private static final String ApiKey= LolApiKey.API_KEY;

    public RankingPageDto getRankingOrderPage()throws ParseException{
        ResponseEntity<String>responseOrder = getResponseEntityByTierOrder();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject =(JSONObject)parser.parse(responseOrder.getBody());
        JSONArray entries =(JSONArray)jsonObject.get("entries");

        RankingPageDto rankingPageDto = new RankingPageDto();

        HashMap<String, Long>tempMap = new HashMap<>();
        for (Object entry : entries) {
            JSONObject summoner = (JSONObject) entry;
            tempMap.put(summoner.get("summonerName").toString(), (Long) summoner.get("leaguePoints"));
        }
        List<String> keySet = new ArrayList<>(tempMap.keySet());
        keySet.sort((o1, o2) -> (int) (tempMap.get(o2) - tempMap.get(o1)));
        JSONArray jArray = new JSONArray();
        int i = 0;
        for(String key : keySet){
            if (i == 18)
                break;
            JSONObject summoner = new JSONObject();
            long point = tempMap.get(key);
            summoner.put("name", key);
            summoner.put("leaguePoints", Long.toString(point));
            addPlayerInfo(summoner, key);
            jArray.add(summoner);
            i++;
        }
        rankingPageDto.setPlayer(jArray);
        System.out.println(rankingPageDto);
        return rankingPageDto;
    }

    public void addPlayerInfo(JSONObject summoner, String name) throws ParseException {
        ResponseEntity<String>response = getResponseEntityByUserName(name);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;

        String encryptedId = (String)jsonObj.get("id");
        summoner.put("summonerLevel", jsonObj.get("summonerLevel").toString());
        summoner.put("icon", myPageImgService.getIconImg(jsonObj.get("profileIconId").toString()));
        summoner.put("tierImg", null);
        setPlayerInfo(summoner, encryptedId);
    }

    public void setPlayerInfo(JSONObject summoner, String userId)throws ParseException{
        ResponseEntity<String>response = getResponseEntityByEncryptedUserId(userId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray)obj;
        JSONObject jsonObj = (JSONObject)jsonArray.get(0);


        int wins = Integer.parseInt(jsonObj.get("wins").toString());
        int losses = Integer.parseInt(jsonObj.get("losses").toString());
        int winRate = (int)((double)wins / (double)(losses + wins) * 100);
        summoner.put("wins", Integer.toString(wins));
        summoner.put("losses", Integer.toString(losses));
        summoner.put("winRate", String.valueOf(winRate)+"%");
        summoner.put("tier", jsonObj.get("tier"));
    }

    private ResponseEntity<String>getResponseEntityByTierOrder() {
        String url = "https://kr.api.riotgames.com/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5";
        url += "?api_key=";
        url += ApiKey;

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

    private ResponseEntity<String> getResponseEntityByEncryptedUserId(String userId){
        String url="https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";
        url+=userId;
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

}

