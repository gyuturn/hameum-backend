package haneum.troller.service.dataDragon;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VersionService {
    private static final String url = "https://ddragon.leagueoflegends.com/api/versions.json";

    public static String getNewVersion()  {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = url;
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl , String.class);
        String[] split = response.getBody().substring(2).split(",");

        return  split[0].substring(0, split[0].length() - 1);
    }
}
