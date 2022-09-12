package haneum.troller.service.fullSearch.GameRecord;

import haneum.troller.service.fullSearch.fullSearchUtil.FullSearchSet;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameRune {
    @Autowired
    private FullSerachUtil fullSerachUtil;

    public String getRuneInfo(JSONArray runeFile, int runeNum, String flag){
        JSONObject runePage = selectRune(runeNum, runeFile);
        JSONArray slots = (JSONArray) runePage.get("slots");
        JSONObject rune = (JSONObject) slots.get(0);
        JSONArray runes = (JSONArray)rune.get("runes");
        return searchRuneInfo(runes, runeNum, flag);
    }

    public String getSemiRuneInfo(JSONArray runeFile, int runeNum, String flag){
        JSONObject semiRunePage = selectRune(runeNum, runeFile);
        if (flag == "icon")
            return "https://ddragon.canisback.com/img/" + (String) semiRunePage.get(flag);
        return (String) semiRunePage.get(flag);
    }

    public String searchRuneInfo(JSONArray runes , int runeNum, String flag){
        for (int i = 0; i < runes.size(); i++){
            JSONObject rune = (JSONObject) runes.get(i);
            if (runeNum == fullSerachUtil.ParseToInt(rune, "id")){
                if (flag == "icon")
                    return "https://ddragon.canisback.com/img/" + (String) rune.get(flag);
                return (String) rune.get(flag);
            }
        }
        return null;

    }

    public JSONObject selectRune(int runeNum, JSONArray runeFile){
        JSONObject runePage = null;
        int div = runeNum - (runeNum % 100);
        if (div == 8100)
            runePage = (JSONObject) runeFile.get(0);
        else if (div == 8300)
            runePage = (JSONObject) runeFile.get(1);
        else if (div == 8000)
            runePage = (JSONObject) runeFile.get(2);
        else if (div == 8400)
            runePage = (JSONObject) runeFile.get(3);
        else if (div == 8200)
            runePage = (JSONObject) runeFile.get(4);
        return runePage;
    }
}
