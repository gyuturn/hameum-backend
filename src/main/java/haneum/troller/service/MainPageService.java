package haneum.troller.service;

import haneum.troller.dto.member.mainPage.MainPageDto;
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
public class MainPageService{

    @Autowired
    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    // APIKEY 는 myPageSerivce 와 같음
    private static final String ApiKey="RGAPI-0910ad71-47e0-46f1-a4d1-37e58adb1bd8";

    public MainPageDto getRankOrder()throws ParseException{
        ResponseEntity<String>response = getResponseEntityByTierOrder();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject =(JSONObject)parser.parse(response.getBody());
        JSONArray entries =(JSONArray)jsonObject.get("entries");

        MainPageDto mainPageDto = new MainPageDto();

        HashMap<String, Integer>tempMap = new HashMap<>();
        for(int i = 0; i < entries.size(); i++){
            JSONObject summoner =(JSONObject)entries.get(i);
            tempMap.put(summoner.get("summonerName").toString(), Integer.parseInt((String)summoner.get("LeaguePoint")));
        }
        List<Map.Entry<String, Integer>>entryList = new LinkedList<>(tempMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        mainPageDto.setRankMap(tempMap);
        return mainPageDto;
    }

    private ResponseEntity<String>getResponseEntityByTierOrder(){
        String url="https://kr.api.riotgames.com/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5/";
        url+="?api_key=";
        url +=ApiKey;

        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",UserAgent);
        headers.set("Accept-Language",AcceptLanguage);
        headers.set("Accept-Charset",AcceptCharset);
        headers.set("Origin",Origin);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String>response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );
        return response;
    }
}