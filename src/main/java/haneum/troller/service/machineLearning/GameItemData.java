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

        JSONObject data = (JSONObject) item.get("data");
        JSONObject itemInfo = new JSONObject();
        int itemInt = fullSerachUtil.ParseToInt(user, "item" + i);
        if (itemInt == 0){
            itemInfo.put("item", "None");
            itemInfo.put("itemImg", "None");
            itemInfo.put("itemNumber", 0);
            itemArray.add(itemInfo);
            return ;
        }
        String dataStr = Integer.toString(fullSerachUtil.ParseToInt(user, "item" + i));
        JSONObject itemData = (JSONObject) data.get(dataStr);
        String itemNameStr = (String)itemData.get("name");
        itemInfo.put("item", itemNameStr);
        itemInfo.put("itemImg", setItemImg(dataStr));
        itemInfo.put("itemNumber", itemInt);
        itemArray.add(itemInfo);
    }
}
