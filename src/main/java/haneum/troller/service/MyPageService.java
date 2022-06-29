package haneum.troller.service;

import com.mysql.cj.xdevapi.JsonArray;
import haneum.troller.dto.member.myPage.MyPageDto;
import haneum.troller.service.dataDragon.MyPageImgService;
import lombok.RequiredArgsConstructor;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


@Service
public class MyPageService {
    @Autowired
    private MyPageImgService myPageImgService;

    private static final String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36";
    private static final String AcceptLanguage="ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
    private static final String AcceptCharset="application/x-www-form-urlencoded; charset=UTF-8";
    private static final String Origin="https://developer.riotgames.com";
    private static final String ApiKey="RGAPI-0910ad71-47e0-46f1-a4d1-37e58adb1bd8";


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


    public MyPageDto getEncryptedLolName(String userName) throws ParseException {
        ResponseEntity<String> response = getResponseEntityByUserName(userName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;

        MyPageDto myPageDto = new MyPageDto();
        myPageDto.setEncryptedLolName((String)jsonObj.get("id"));
        myPageDto.setName((String)jsonObj.get("name"));
        myPageDto.setIcon(myPageImgService.getIconImg(jsonObj.get("profileIconId").toString()));
        myPageDto.setLevel(jsonObj.get("summonerLevel").toString());

        return myPageDto;
    }

    public MyPageDto getMyPageAttr(MyPageDto myPageDto) throws ParseException, IOException {
        ResponseEntity<String> response = getResponseEntityByEncryptedUserId(myPageDto.getEncryptedLolName());
        
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray)obj;
        JSONObject jsonObj = (JSONObject)jsonArray.get(0);



        myPageDto.setWin(jsonObj.get("wins").toString());
        myPageDto.setLose(jsonObj.get("losses").toString());
        int wins = Integer.parseInt(jsonObj.get("wins").toString());
        System.out.println("wins = " + wins);
        int losses = Integer.parseInt(jsonObj.get("losses").toString());
        System.out.println("losses = " + losses);
        int winRate = (int)((double)wins / (double)(losses + wins) * 100);
        System.out.println("winRate = " + winRate);
        myPageDto.setWinRate(String.valueOf(winRate)+"%");

        myPageDto.setTier(jsonObj.get("tier").toString());
//        System.out.println("jsonObj.get(\"tier\").toString() = " + jsonObj.get("tier").toString());
//        File sourceImage = new File("./dataDragon/tierImg/"+jsonObj.get("tier").toString()+".png");
//        System.out.println("sourceImage = " + sourceImage);
//        Image tierImage = ImageIO.read(sourceImage);
//        myPageDto.setTierImg(tierImage);
//        티어 이미지경로를 모르겠음

        myPageDto.setRank(jsonObj.get("rank").toString());
        myPageDto.setPoint(jsonObj.get("leaguePoints").toString());

        return myPageDto;



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
