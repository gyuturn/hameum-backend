package haneum.troller.service.getRiotApi;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GetRiotApiUtil {

    @Autowired
    public GetRiotApi getRiotApi;
    private String summonerName;

    public String getUserPid(String lolName) throws org.json.simple.parser.ParseException {
        ResponseEntity<String> response = getRiotApi.getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;

        String userPid = (String)jsonObj.get("puuid");
        return userPid;
    }

    public ArrayList getMatchId(String pid, int count) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getRiotApi.getResponseEntityByUserPid(pid, count);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray) obj;
        ArrayList matchList = new ArrayList<>(count);
        for (int i = 0; i < count; i++){
            matchList.add(jsonArray.get(i));
        }
        return matchList;
    }

    public ArrayList machineLearningGetMatchId(String pid, int count, String type) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getRiotApi.getResponseEntityByUserPidType(pid, count, type);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray) obj;
        ArrayList matchList = new ArrayList<>(count);
        for (int i = 0; i < count; i++){
            matchList.add(jsonArray.get(i));
            System.out.println("i = " + i);
        }
        return matchList;
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

    public String getEncryptedId(String lolName) throws org.json.simple.parser.ParseException {
        ResponseEntity<String>response = getRiotApi.getResponseEntityByUserName(lolName);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject)obj;
        String summonerId = (String)jsonObj.get("id");
        return summonerId;
    }

    private String getSummonerName() {
        return summonerName;
    }
}
