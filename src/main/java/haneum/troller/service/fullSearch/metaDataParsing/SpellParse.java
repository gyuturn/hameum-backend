package haneum.troller.service.fullSearch.metaDataParsing;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Service
public class SpellParse implements Parse{
    public String getSpellInfo(JSONObject spellFile ,int spellNum, String flag){
        JSONObject data = (JSONObject) spellFile.get("data");
        JSONObject spellObj = new JSONObject(data);
        ArrayList<String>spellKeyList = new ArrayList<>();
        Iterator<String> i = spellObj.keySet().iterator();
        while (i.hasNext()){
            String b = i.next().toString();
            JSONObject spellData = (JSONObject) data.get(b);
            int key = ParseToInt(spellData, "key");
            if (key == spellNum) {
                String spellName = (String) spellData.get("name");
                if (flag == "icon")
                    return "https://ddragon.leagueoflegends.com/cdn/10.6.1/img/spell/" + spellName + ".png";
                return spellName;
            }
        }
        return null;
    }

    public int ParseToInt(JSONObject obj, String str){
        int  retNum = 0;
        retNum = Integer.parseInt(String.valueOf(obj.get(str)));
        return retNum;
    }
}
