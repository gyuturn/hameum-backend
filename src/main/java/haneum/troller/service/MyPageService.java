package haneum.troller.service;

import haneum.troller.dto.member.myPage.MyPageDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class MyPageService {
    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    private static final String ApiKey="RGAPI-cfdca04f-cb01-4cbf-86a4-e7279a34ef4e";


    public boolean checkLolName(String userName) throws ParseException {
        ResponseEntity<String> response = getResponseEntityByUserName(userName);

        boolean result=false;
        try{
            if(response.getStatusCode().toString().equals("200 OK")){
                result=true;
            }
        }catch (Exception e){
            result=false;
        }
        finally {
            return result;
        }

    }

    public void buildURI(String userName) throws ParseException {
        ResponseEntity<String> response = getResponseEntityByUserName(userName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;

        MyPageDto myPageDto = new MyPageDto();
        myPageDto.setName((String)jsonObj.get("name"));
        myPageDto.setName((String)jsonObj.get("profileIconId"));
        myPageDto.setName((String)jsonObj.get("summonerLevel"));
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
