package haneum.troller.service.fullSearch;

import haneum.troller.common.apiKey.LolApiKey;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GetRiotApi {
    public static final String ApiKey= LolApiKey.randomApiKey();

    public String getResponseEntityByMatchId(String matchId) {
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/";
        url += matchId;
        url += "?api_key=";
        url += ApiKey;

        HttpResponse response;
        String entity;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);

            response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            entity = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return entity;
    }
}
