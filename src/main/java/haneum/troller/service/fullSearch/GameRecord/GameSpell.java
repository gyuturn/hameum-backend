package haneum.troller.service.fullSearch.GameRecord;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GameSpell {

    public void setSpellInfo(JSONObject userRecord , int spellNum, String flag){
        switch (spellNum){
            case 21:
                userRecord.put(flag, "방어막");
                userRecord.put(flag + "img", setSpellImg("SummonerBarrier"));
                break;
            case 1:
                userRecord.put(flag, "정화");
                userRecord.put(flag + "img", setSpellImg("SummonerBoost"));
                break;
            case 14:
                userRecord.put(flag, "점화");
                userRecord.put(flag + "img", setSpellImg("SummonerDot"));
                break;
            case 3:
                userRecord.put(flag, "탈진");
                userRecord.put(flag + "img", setSpellImg("SummonerExhaust"));
                break;
            case 4:
                userRecord.put(flag, "점멸");
                userRecord.put(flag + "img", setSpellImg("SummonerFlash"));
                break;
            case 6:
                userRecord.put(flag, "유체화");
                userRecord.put(flag + "img", setSpellImg("SummonerHaste"));
                break;
            case 7:
                userRecord.put(flag, "회복");
                userRecord.put(flag + "img", setSpellImg("SummonerHeal"));
                break;
            case 13:
                userRecord.put(flag, "총명");
                userRecord.put(flag + "img", setSpellImg("SummonerMana"));
                break;
            case 31:
                userRecord.put(flag, "포로 던지기");
                userRecord.put(flag + "img", setSpellImg("SummonerPoroThrow"));
                break;
            case 11:
                userRecord.put(flag, "강타");
                userRecord.put(flag + "img", setSpellImg("SummonerSmate"));
                break;
            case 32:
                userRecord.put(flag, "표식");
                userRecord.put(flag + "img", setSpellImg("SummonerSnowball"));
                break;
            case 12:
                userRecord.put(flag, "텔레포트");
                userRecord.put(flag + "img", setSpellImg("SummonerTeleport"));
                break;
        }
    }

    public String setSpellImg(String img){
        return "http://ddragon.leagueoflegends.com/cdn/12.15.1/img/spell/" + img + ".png";
    }



}
