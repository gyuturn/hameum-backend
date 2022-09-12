package haneum.troller.service.fullSearch.fullSearchUtil;

import haneum.troller.service.fullSearch.GameRecord.GameTwentyRecord;
import haneum.troller.service.fullSearch.mostThreeChampion.GameMostChampionRecord;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class FullSearchSet {

    @Autowired
    public FullSerachUtil fullSerachUtil;

    public void setKdaWinRate(JSONObject user, GameMostChampionRecord gameMostChampionRecord){

        int kill = fullSerachUtil.ParseToInt(user ,"kills");
        int death = fullSerachUtil.ParseToInt(user ,"deaths");
        int assist = fullSerachUtil.ParseToInt(user, "assists");
        int win = 0;
        int draw = 0;
        int lose = 0;

        gameMostChampionRecord.setKill(gameMostChampionRecord.getKill() + kill);
        gameMostChampionRecord.setDeath(gameMostChampionRecord.getDeath() + death);
        gameMostChampionRecord.setAssist(gameMostChampionRecord.getAssist() + assist);
        if (user.get("win") == TRUE)
            win = 1;
        else
            lose = 1;
        if (user.get("teamEarlySurrendered") == TRUE)
            draw = 1;
        gameMostChampionRecord.setWin((gameMostChampionRecord.getWin()) + win);
        gameMostChampionRecord.setLose(gameMostChampionRecord.getLose() + lose);
        gameMostChampionRecord.setDraw((gameMostChampionRecord.getDraw()) + draw);
        return ;
    }

    public void setKdaWinRateTwenty(JSONObject user, GameTwentyRecord twentyRecord){

        int kill = fullSerachUtil.ParseToInt(user, "kills");
        int death = fullSerachUtil.ParseToInt(user, "deaths");
        int assist = fullSerachUtil.ParseToInt(user, "assists");

        int win = 0;
        int lose = 1;
        int draw = 0;

        twentyRecord.setKill(twentyRecord.getKill() + kill);
        twentyRecord.setDeath(twentyRecord.getDeath() + death);
        twentyRecord.setAssist(twentyRecord.getAssist() + assist);
        if (user.get("win") == TRUE){
            win = 1;
            lose = 0;
        }
        if (user.get("teamEarlySurrendered") == TRUE)
            draw = 1;
        twentyRecord.setWin((twentyRecord.getWin()) + win);
        twentyRecord.setLose((twentyRecord.getLose()) + lose);
        twentyRecord.setDraw((twentyRecord.getDraw()) + draw);
        return ;
    }

    public void setCs(JSONObject user, GameMostChampionRecord gameMostChampionRecord, int playTime){
        int cs = fullSerachUtil.ParseToInt(user ,"neutralMinionsKilled") +
                fullSerachUtil.ParseToInt(user, "totalMinionsKilled");
        double csPerMinutes = Math.round((double)cs / ((double)playTime / 60) * 10) / 10.0;
        gameMostChampionRecord.setCs(cs);
        gameMostChampionRecord.setCsPerMinutes(csPerMinutes);
    }

    public JSONObject setKdaWinRateDto(GameTwentyRecord gameTwentyRecord, JSONObject json){
        double avgKill = getAvgKda(gameTwentyRecord.getKill());
        double avgAssist = getAvgKda(gameTwentyRecord.getAssist());
        double avgDeath = getAvgKda(gameTwentyRecord.getDeath());
        String kdaRound = Double.toString(gameTwentyRecord.getCalculatedKda());
        if (kdaRound.length() >= 5)
            kdaRound = kdaRound.substring(0, 4);
        String winRound = Integer.toString((int)(gameTwentyRecord.getCalculatedWinRate() * 100));
        json.put("averageKill", Double.toString(avgKill));
        json.put("averageDeath", Double.toString(avgDeath));
        json.put("averageAssist", Double.toString(avgAssist));
        json.put("win", Integer.toString(gameTwentyRecord.getWin()));
        json.put("lose", Integer.toString(gameTwentyRecord.getLose()));
        json.put("draw", Integer.toString(gameTwentyRecord.getDraw()));
        json.put("winRate", winRound + "%");
        json.put("averageKda",  kdaRound);
        return json;
    }

    public Double getAvgKda(int killOrDeathOrAssist){

        if (killOrDeathOrAssist == 0)
            return (double)0;
        double n = ((double)killOrDeathOrAssist / 20) * 10;
        n = Math.round(n);
        double avg = n / 10 ;
        return avg;
    }
}
