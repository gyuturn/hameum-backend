package haneum.troller.service.machineLearning;

import haneum.troller.service.fullSearch.GameRecord.GameSpell;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GameSpellData extends GameSpell {

    @Override
    public void setSpellInfo(JSONObject userRecord , int spellNum, String flag){
        super.setSpellInfo(userRecord, spellNum, flag);
        userRecord.put("spellNumber" ,spellNum);
    }
}
