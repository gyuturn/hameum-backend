package haneum.troller.service.fullSearch.GameRecord;

import haneum.troller.service.fullSearch.fullSearchUtil.FullSearchSet;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class MatchDataSet {

    @Autowired
    private FullSerachUtil fullSerachUtil;
    @Autowired
    private GameSpell gameSpell;
    @Autowired
    private GameItem gameItem;
    @Autowired
    private GameRune gameRune;

    public void matchKdaAndWinRecord(JSONArray participants, JSONObject user, JSONObject userRecord) {

        int kill = fullSerachUtil.ParseToInt(user, "kills");
        int death = fullSerachUtil.ParseToInt(user, "deaths");
        int assist = fullSerachUtil.ParseToInt(user, "assists");
        userRecord.put("kill", Integer.toString(kill));
        userRecord.put("death", Integer.toString(death));
        userRecord.put("assist", Integer.toString(assist));
        double kda = 0;
        if (death == 0)
            userRecord.put("kda", "perfect");
        else {
            kda = ((double)kill + (double)assist / (double)death) * 100;
            kda = Math.round(kda);
            kda = kda / 100;
            userRecord.put("kda", Double.toString(kda));
        }
        // -> k/d/a 및 kda 세팅
        if (user.get("win") == TRUE)
            userRecord.put("win", TRUE);
        else
            userRecord.put("win", FALSE); // 승리여부
    }

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
        gameSpell.setSpellInfo(userRecord, fullSerachUtil.ParseToInt(user, "summoner1Id"), "spell1");
        gameSpell.setSpellInfo(userRecord, fullSerachUtil.ParseToInt(user, "summoner2Id"), "spell2");
        // -> 스펠 셋팅 완료
        JSONArray itemArray = new JSONArray();
        for (int i = 0; i < 7; i++){
            gameItem.setItemInfo(item, itemArray, user, i);
        }
        userRecord.put("itemArray" ,itemArray);
    }

    public String playTimeFormatting(int second){
        int h = second / 3600;
        int m = (second - (h * 3600)) / 60;
        int s = second % 60;
        String ret = null;
        if (h != 0) {
            ret = String.valueOf(h) + "시" + String.valueOf(m) + "분" + String.valueOf(s) + "초";
        }
        else{
            ret = String.valueOf(m) + "분" + String.valueOf(s) + "초";
        }
        return ret;
    }

    public String lastPlayTimeFormatting(int second){
        int month = second / (30 * 86400);
        int week = (second - (month * 30 * 86400)) / (7 * 86400);
        int date = ((second - (month * 30 * 86400)) - week * 7 * 86400) / 86400;
        int h = (second % 86400) / 3600;
        int m = ((second % (86400)) % 3600) % 60;
        String ret;
        if (month != 0)
            ret = String.valueOf(month) + "달 전";
        else if (week != 0)
            ret = String.valueOf(week) + "주 전";
        else if (date != 0)
            ret = String.valueOf(date) + "일 전";
        else if (h != 0)
            ret = String.valueOf(h) + "시간 전";
        else
            ret = String.valueOf(m) + "분 전";
        return ret;
    }

    public int matchPlayTime(JSONObject info, JSONObject userRecord){
        int playTime = fullSerachUtil.ParseToInt(info, "gameDuration");
        long playBefore = Instant.now().getEpochSecond() - Math.round((Long.parseLong(String.valueOf(info.get("gameEndTimestamp"))) / 1000));
        int h = playTime / 3600;
        int m = (playTime - (h * 3600)) / 60;
        userRecord.put("playtimeMinutes", m);
        userRecord.put("playtime", playTimeFormatting(playTime));
        userRecord.put("lastPlayTime", lastPlayTimeFormatting((int)playBefore));
        return playTime;
    }

    public void matchCsAndWard(JSONObject user, JSONObject userRecord, int playTime){
        int cs = fullSerachUtil.ParseToInt(user, "neutralMinionsKilled") + fullSerachUtil.ParseToInt(user, "totalMinionsKilled");
        double csPerMinutes = (double)cs / ((double)playTime / 60) * 10;
        csPerMinutes = Math.round(csPerMinutes);
        csPerMinutes /= 10;
        int visionWard = fullSerachUtil.ParseToInt(user, "visionWardsBoughtInGame");
        userRecord.put("cs", String.valueOf(cs));
        userRecord.put("csPerMinutes", String.valueOf(csPerMinutes));
        userRecord.put("visionWard", String.valueOf(visionWard));
    }
}
