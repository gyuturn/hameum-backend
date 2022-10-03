package haneum.troller.common.callApi;


import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class CallApi {


    public static ResponseEntity PostIncludeObject(String apienv, String addUrl, String jsonToString){
        //ResTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        //헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        headers.add("Content-Type", "application/json;charset=UTF-8");

        //url 생성
        URI url = URI.create(apienv+addUrl);

        //POST로 보내는 경우 : body에 실어보낼 json데이터 생성
        HttpEntity<String> entity = new HttpEntity<>(jsonToString, headers);
        return  restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    }

    public static ResponseEntity GetIncludeParameter(String addUrl) {
        //ResTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        //헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        headers.add("Content-Type", "application/json;charset=UTF-8");

        //url 생성
        URI url = URI.create(APIENV.APPURL+addUrl);

        //GET으로 보내는 경우 : 쿼리파라미터는 url에 붙여 보내면 댐
        System.out.println("url = " + url);
        RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
        return restTemplate.exchange(req, String.class);
    }

}