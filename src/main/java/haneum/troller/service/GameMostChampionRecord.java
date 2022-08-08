package haneum.troller.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMostChampionRecord implements Comparable<GameMostChampionRecord> {

    private int gamePlayed; // setChampionClass
    private String championName; //set
    private int win; //setKdaWinRate
    private int draw; //setKdaWinRate
    private int lose; //setKdaWinRate
    private double winRate;
    private int kill; //setKdaWinRate
    private int death; //setKdaWinRate
    private int assist; //setKdaWinRate
    private int cs; //setCs
    private double csPerMinutes; //setCs
    private String ui; // setChampionClass
    private double kda;
    {
        win = 0;
        draw = 0;
        lose = 0;
        kill = 0;
        death = 0;
        assist = 0;
        cs = 0;
        csPerMinutes = 0;
        gamePlayed = 0;
        ui = "ui";
        championName = "name";
    }


    public Double getCalculatedWinRate(){
        winRate = ((double)(win) / (double)(gamePlayed - draw)) * 100;
        return winRate;
    }

    public Double getCalculatedKda(){
        kda = ((double)(kill + assist) / (double)death);
        return kda;
    }

    @Override // arraylist 내에 class 를 gamePlayed를 기준으로 분류
    public int compareTo(GameMostChampionRecord gameMostChampionRecord) {
        if (this.gamePlayed < gameMostChampionRecord.getGamePlayed())
            return -1;
        else if (this.gamePlayed > gameMostChampionRecord.getGamePlayed())
            return 1;
        return 0;
    }
}
