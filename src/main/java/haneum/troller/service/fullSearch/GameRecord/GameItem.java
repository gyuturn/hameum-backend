package haneum.troller.service.fullSearch.GameRecord;

import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
import haneum.troller.service.getRiotApi.GetRiotApiUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameItem {

    @Autowired
    private FullSerachUtil fullSerachUtil;

    public String setItemImg(String img){
        return "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/item/" + img + ".png";
    }

    public void setItemInfo(JSONObject item, JSONArray itemArray, JSONObject user, int i){

        JSONObject data = (JSONObject) item.get("data");
        JSONObject itemInfo = new JSONObject();
        int itemInt = fullSerachUtil.ParseToInt(user, "item" + i);
        if (itemInt == 0){
            itemInfo.put("item", "None");
            itemInfo.put("itemImg", "None");
            itemArray.add(itemInfo);
            return ;
        }
        String dataStr = Integer.toString(fullSerachUtil.ParseToInt(user, "item" + i));
        JSONObject itemData = (JSONObject) data.get(dataStr);
        String itemNameStr = (String)itemData.get("name");
        itemInfo.put("item", itemNameStr);
        itemInfo.put("itemImg", setItemImg(dataStr));
        itemArray.add(itemInfo);
    }
}
