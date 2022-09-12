package haneum.troller.service.machineLearning;

import haneum.troller.service.fullSearch.GameRecord.GameItem;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSearchSet;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameItemData extends GameItem {

    @Autowired
    private FullSerachUtil fullSerachUtil;

    @Override
    public void setItemInfo(JSONObject item, JSONArray itemArray, JSONObject user, int i){
        super.setItemInfo(item, itemArray, user, i);
        JSONObject data = (JSONObject) item.get("data");
        JSONObject itemInfo = new JSONObject();
        int itemInt = fullSerachUtil.ParseToInt(user, "item" + i);
        itemInfo.put("itemNumber", 0);
    }
}
