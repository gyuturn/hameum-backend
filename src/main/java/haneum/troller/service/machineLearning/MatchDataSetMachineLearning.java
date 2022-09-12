package haneum.troller.service.machineLearning;

import haneum.troller.service.fullSearch.GameRecord.GameRune;
import haneum.troller.service.fullSearch.GameRecord.GameSpell;
import haneum.troller.service.fullSearch.GameRecord.MatchDataSet;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MatchDataSetMachineLearning extends MatchDataSet {

    @Autowired
    private FullSerachUtil fullSerachUtil;
    @Autowired
    private GameItemData gameItemData;
    @Autowired
    private GameSpellData gameSpellData;
    @Autowired
    private GameRune gameRune;

    @Override
    public void matchMetaDataSetting(JSONObject user, JSONObject userRecord, JSONArray rune, JSONObject spell, JSONObject item) throws IOException, org.json.simple.parser.ParseException {

        String primaryRune = null;
        String primaryRuneImg = null;
        JSONObject perks = (JSONObject) user.get("perks");
        JSONArray styles = (JSONArray)perks.get("styles");
        JSONObject stylesObj = (JSONObject) styles.get(0);
        JSONArray selections = (JSONArray) stylesObj.get("selections");
        JSONObject primary = (JSONObject)selections.get(0);
        JSONObject semi = (JSONObject)styles.get(1);
        int primaryRuneNum = fullSerachUtil.ParseToInt(primary ,"perk");
        int semiRuneNum = fullSerachUtil.ParseToInt(semi, "style");
        if (primaryRuneNum == 9923) {
            primaryRune = "칼날비";
            primaryRuneImg = "https://ddragon.canisback.com/img/perk-images/Styles/Domination/HailOfBlades/HailOfBlades.png";
        }
        else{
            primaryRune = gameRune.getRuneInfo(rune, primaryRuneNum, "name");
            primaryRuneImg = gameRune.getRuneInfo(rune, primaryRuneNum, "icon");
        }
        String semiRune = gameRune.getSemiRuneInfo(rune, semiRuneNum, "name");
        String semiRuneImg = gameRune.getSemiRuneInfo(rune, semiRuneNum, "icon");
        userRecord.put("primaryRune",primaryRune);
        userRecord.put("primaryRuneImg", primaryRuneImg);
        userRecord.put("semiRune", semiRune);
        userRecord.put("semiRuneImg", semiRuneImg);
        // -> 룬 셋팅 완료
        gameSpellData.setSpellInfo(userRecord, fullSerachUtil.ParseToInt(user, "summoner1Id"), "spell1");
        gameSpellData.setSpellInfo(userRecord, fullSerachUtil.ParseToInt(user, "summoner2Id"), "spell2");
        // -> 스펠 셋팅 완료
        JSONArray itemArray = new JSONArray();
        for (int i = 0; i < 7; i++){
            gameItemData.setItemInfo(item, itemArray, user, i);
        }
        userRecord.put("itemArray" ,itemArray);
    }
}
