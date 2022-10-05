package haneum.troller.service.findDuo;

import haneum.troller.dto.findDuo.FindDuoResponseDto;
import haneum.troller.service.dataDragon.ChampionImgService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
public class FindDuoSetUtil {

    @Autowired
    private ChampionImgService championImgService;

    public void setMostThree(ArrayList champions, FindDuoResponseDto findDuoDto)
    {
        String champion = null;
        ArrayList<String> championArray = new ArrayList<>(3);
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 20; i++){
            champion = (String)champions.get(i);
            if (map.containsKey(champion) == TRUE)
                map.replace(champion, map.get(champion) + 1);
            else
                map.put(champion, 1);
        }
        List<String> keySet = new ArrayList<>(map.keySet());
        keySet.sort((o1, o2) -> (int) (map.get(o2) - map.get(o1)));
        int i = 0;
        for (String key : keySet){
            championArray.add(championImgService.getChampionImg(String.valueOf(map.get(key))));
            if (i == 0)
                findDuoDto.setChampion1(championImgService.getChampionImg(key));
            if (i == 1)
                findDuoDto.setChampion2(championImgService.getChampionImg(key));
            if (i == 2)
                findDuoDto.setChampion3(championImgService.getChampionImg(key));
            i++;
            if (i == 3)
                break;
        }
    }

    public String matchLinePreference(ArrayList position){
        JSONObject linePreference = new JSONObject();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("TOP", 0);
        map.put("JUNGLE", 0);
        map.put("MID", 0);
        map.put("BOTTOM", 0);
        map.put("UTILITY", 0);
        for (Object line : position){
            switch ((String)line){

                case "TOP":
                    map.replace("TOP", map.get("TOP") + 1);
                    break;
                case "JUNGLE":
                    map.replace("JUNGLE", map.get("JUNGLE") + 1);
                    break;
                case "MID":
                    map.replace("MID", map.get("MID") + 1);
                    break;
                case "BOTTOM":
                    map.replace("BOTTOM", map.get("BOTTOM") + 1);
                    break;
                case "UTILITY":
                    map.replace("UTILITY", map.get("UTILITY") + 1);
                    break;
            }
        }
        List<String> keySet = new ArrayList<>(map.keySet());
        keySet.sort((o1, o2) -> map.get(o2).compareTo(map.get(o1)));
        int i = 0;
        String line = null;
        for (String key : keySet) {
            if (i == 0) {
                line = String.valueOf(map.get(key));
                System.out.println("key = " + key);
                if (key == "TOP")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-top-blue.png";
                else if (key == "JUNGLE")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-jungle-blue.png";
                else if (key == "MID")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-mid-blue.png";
                else if (key == "BOTTOM")
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-bottom-blue.png";
                else
                    line = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-utility-blue.png";
                break;
            }
        }
        return line;
    }

    public JSONObject stringToJson(String jsonStr) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(jsonStr);
    }
}
