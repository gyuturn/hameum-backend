package haneum.troller.service.fullSearch.mostThreeChampion;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MostChampUtil {

    public boolean isContain(ArrayList champions, String className){
        GameMostChampionRecord game = null;
        String compare = null;
        for(int i = 0; i < champions.size(); i++){
            game = (GameMostChampionRecord) champions.get(i);
            compare = game.getChampionName();
            if (compare.compareTo(className) == 0) {
                return true;
            }
        }
        return false;
    }

    public int  getIndexOf(ArrayList champions, String className){
        GameMostChampionRecord game = null;
        String compare = null;
        for(int i = 0; i < champions.size(); i++){
            game = (GameMostChampionRecord) champions.get(i);
            compare = game.getChampionName();
            if (compare.compareTo(className) == 0) {
                return i;
            }
        }
        return -1;
    }

    public JSONObject getUserFromJson(JSONArray participants, String lolName){
        JSONObject user = null;
        String name = null;
        for (int i = 0; i < 10; i++){
            user = (JSONObject) participants.get(i);
            name = (String)user.get("summonerName");
            if (name.equals(lolName)) {
                break;
            }
        }
        return user;
    }

}
