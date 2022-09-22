package haneum.troller.service.fullSearch.GameRecord;

import haneum.troller.service.dataDragon.ChampionImgService;
import haneum.troller.service.fullSearch.fullSearchUtil.FullSerachUtil;
import haneum.troller.service.getRiotApi.GetRiotApi;
import haneum.troller.service.getRiotApi.GetRiotApiUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.FALSE;

@Service
public class MatchPlayer {

    @Autowired
    private ChampionImgService championImgService;
    @Autowired
    private GetRiotApiUtil getRiotApiUtil;
    @Autowired
    private GetRiotApi getRiotApi;
    @Autowired
    private FullSerachUtil fullSerachUtil;

    public JSONArray setPlayers(JSONObject user,JSONArray participants) throws org.json.simple.parser.ParseException {
        double avgTier = 0; // save average tier of players
        JSONArray players = new JSONArray();
        for (int i = 0; i < 10; i++){
            getPlayers((JSONObject)participants.get(i), players, i); // get player's info
        }
        user.put("Tier", IntToTier((int)avgTier));
        return players;
    }

    public void getPlayers(JSONObject participant, JSONArray players, int teamIndex) throws org.json.simple.parser.ParseException {
        JSONObject player = new JSONObject();
        player.put("lolName", (String)participant.get("summonerName"));
        String championName = (String)participant.get("championName");
        player.put("championName", (String)championName);
        player.put("championImg", championImgService.getChampionImg(championName));
        player.put("Position", (String)participant.get("teamPosition"));
        if (teamIndex < 5)
            player.put("team", "Blue");
        else
            player.put("team", "Red");
        players.add(player);
    }

    public String IntToTier(int point){
        if (point >= 0 && point <= 4)
            return "IRON";
        else if (point >= 5 && point <= 8)
            return "BRONZE";
        else if (point >= 9 && point <= 12)
            return "SILVER";
        else if (point >= 13 && point <= 16)
            return "GOLD";
        else if (point >= 17 && point <= 20)
            return "PLATINUM";
        else if (point >= 21 && point <= 24)
            return "DIAMOND";
        else if (point >= 25 && point <= 35)
            return "MASTER";
        else if (point >= 36 && point <= 45)
            return "GRANDMASTER";
        else
            return "CHALLNEGER";
    }

    public void  getAvgTier(String lolName, JSONObject userRecord) throws org.json.simple.parser.ParseException {
        String summonerId = getRiotApiUtil.getEncryptedId(lolName);
        ResponseEntity<String> response = getRiotApi.getResponseEntityByEncryptedUserId(summonerId);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONArray jsonArray = (JSONArray)obj;
        JSONObject jsonObj;
        jsonObj = (JSONObject)jsonArray.get(0);
        String cmpGame = (String)jsonObj.get("queueType");
        if (cmpGame.equals("RANKED_SOLO_5x5") == FALSE)
            jsonObj = (JSONObject)jsonArray.get(1);
        String tier = (String)jsonObj.get("tier");
        String rank = (String)jsonObj.get("rank");
        if (tier.equals("MASTER") || tier.equals("GRANDMASTER") || tier.equals("CAHLLENGER"))
            userRecord.put("averageTier", tier);
        else
            userRecord.put("averageTier", tier + rank);
    }

    public void getKillRate(JSONObject info, JSONObject user ,JSONObject userRecord, GameTwentyRecord twentyRecord){
        int kill = fullSerachUtil.ParseToInt(user, "kills");
        int userTeamId = fullSerachUtil.ParseToInt(user ,"teamId");
        int totalKill = 0;
        int killRate;

        JSONArray team = (JSONArray)info.get("teams");
        JSONObject teamToObj = (JSONObject)team.get(0);
        JSONObject objectives = (JSONObject) teamToObj.get("objectives");
        JSONObject objectChamp;
        if (userTeamId == 100){
            objectChamp = (JSONObject) objectives.get("champion");
            totalKill = fullSerachUtil.ParseToInt(objectChamp ,"kills");
        }
        else{
            teamToObj = (JSONObject)team.get(1);
            objectives = (JSONObject) teamToObj.get("objectives");
            objectChamp = (JSONObject) objectives.get("champion");
            totalKill = totalKill = fullSerachUtil.ParseToInt(objectChamp ,"kills");
        }
        killRate = (int)(Math.round((double) kill / (double) totalKill * 100));
        twentyRecord.setTotalKillRelated(twentyRecord.getTotalKillRelated() + killRate);
        userRecord.put("killRate", String.valueOf(killRate)+"%"); // 킬관여 세팅
    }

}
