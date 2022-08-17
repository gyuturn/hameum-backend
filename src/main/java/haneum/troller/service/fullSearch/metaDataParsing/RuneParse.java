package haneum.troller.service.fullSearch.metaDataParsing;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RuneParse {
    public String getRuneInfo(JSONArray runeFile, int runeNum, String flag){
        System.out.println("akakakakakakk");
        JSONObject runePage = selectRune(runeNum, runeFile);
        System.out.println("debug");
        JSONArray slots = (JSONArray) runePage.get("slots");
        JSONObject rune = (JSONObject) slots.get(0);
        JSONArray runes = (JSONArray)rune.get("runes");
        return searchRuneInfo(runes, runeNum, flag);
    }

    public String getSemiRuneInfo(JSONArray runeFile, int runeNum, String flag){
        JSONObject semiRunePage = selectRune(runeNum, runeFile);
        return (String) semiRunePage.get(flag);
    }

    public String searchRuneInfo(JSONArray runes , int runeNum, String flag){
        for (int i = 0; i < runes.size(); i++){
            JSONObject rune = (JSONObject) runes.get(i);
            System.out.println((String) rune.get(flag));
            if (runeNum == ParseToInt(rune, "id")){
                return (String) rune.get(flag);
            }
        }
        return null;

    }

    public JSONObject selectRune(int runeNum, JSONArray runeFile){
        JSONObject runePage = null;
        System.out.println("runeNum = " + runeNum);
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

    public int ParseToInt(JSONObject obj, String str){
        int  retNum = 0;
        retNum = Integer.parseInt(String.valueOf(obj.get(str)));
        return retNum;
    }
}
