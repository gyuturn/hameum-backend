package haneum.troller.service.fullSearch.fullSearchUtil;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class FullSerachUtil {

    public int ParseToInt(JSONObject obj, String str){
        int  retNum = 0;
        retNum = Integer.parseInt(String.valueOf(obj.get(str)));
        return retNum;
    }


}
