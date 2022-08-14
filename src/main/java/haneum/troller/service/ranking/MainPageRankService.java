package haneum.troller.service.ranking;

import haneum.troller.common.apiKey.LolApiKey;
import haneum.troller.dto.mainPage.MainPageDto;
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
public class MainPageRankService {

    @Autowired
    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    private static final String ApiKey= LolApiKey.API_KEY;

    public MainPageDto getRankOrder()throws ParseException{
        ResponseEntity<String>response = getResponseEntityByTierOrder();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject =(JSONObject)parser.parse(response.getBody());
        JSONArray entries =(JSONArray)jsonObject.get("entries");

        MainPageDto mainPageDto = new MainPageDto();

        HashMap<String, Long>tempMap = new HashMap<>();
        for(int i = 0; i < entries.size(); i++){
            JSONObject summoner =(JSONObject)entries.get(i);
            tempMap.put(summoner.get("summonerName").toString(), (Long) summoner.get("leaguePoints"));
        }
        List<String> keySet = new ArrayList<>(tempMap.keySet());
//        for (Entry<String, Long> entrySet : tempMap.entrySet()) {
////            System.out.println(entrySet.getKey() + " : " + entrySet.getValue());
//        }
        keySet.sort((o1, o2) -> (int) (tempMap.get(o2) - tempMap.get(o1)));
        JSONArray jArray = new JSONArray();
        int i = 0;
        for(String key : keySet){
            if (i == 10)
                break;
            JSONObject summoner = new JSONObject();
            long point = tempMap.get(key);
            summoner.put("name", key);
            summoner.put("leaguePoints", Long.toString(point));
            jArray.add(summoner);
            i++;
        }
        mainPageDto.setEntries(jArray);
        return mainPageDto;
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
}