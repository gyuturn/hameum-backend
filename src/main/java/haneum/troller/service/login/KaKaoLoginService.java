package haneum.troller.service.login;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import haneum.troller.common.exception.exceptions.KakaoLoginException;
import haneum.troller.dto.jwtDto.JwtDto;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class KaKaoLoginService {

    public JwtDto getKakaoAccessToken(String code) throws Exception {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";


        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id= f0ce58d3396972023535ddbccd0e5f30"); // TODO REST_API_KEY 입력
        sb.append("&redirect_uri=http://localhost:3000/auth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
        sb.append("&code=" + code);
        bw.write(sb.toString());
        bw.flush();

        //결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        if (!(responseCode == 200)) {
            throw new KakaoLoginException("카카오 인증서버와의 로직에서 에러");
        }
        //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }
        System.out.println("response body : " + result);

        //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);

        access_Token = element.getAsJsonObject().get("access_token").getAsString();
        refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

        System.out.println("access_token -kakao: " + access_Token);
        System.out.println("refresh_token -kakao : " + refresh_Token);

        br.close();
        bw.close();

        JwtDto jwtDto = JwtDto.builder()
                .accessToken(access_Token)
                .refreshToken(refresh_Token)
                .build();

        return jwtDto;
    }


    public String getEmailByAccessToken(String accessToken) throws Exception {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        String email = null;

        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + accessToken); //전송할 header 작성, access_token전송

        //결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        if (!(responseCode == 200)) {
            throw new KakaoLoginException("카카오 인증서버와의 로직에서 에러");
        }

        //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }
        System.out.println("response body : " + result);

        //Gson 라이브러리로 JSON파싱
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);

        int id = element.getAsJsonObject().get("id").getAsInt();
        boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
        email = "";
        if (hasEmail) {
            email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
        }


        return email;


    }
}



