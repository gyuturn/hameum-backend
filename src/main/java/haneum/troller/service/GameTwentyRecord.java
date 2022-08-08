package haneum.troller.service;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class GameTwentyRecord {

    private int win = 0;

    private int lose = 0;

    private int draw = 0;

    private double winRate;

    private int kill = 0;

    private int death = 0;

    private int assist = 0;

    private double kda;

    public double getCalculatedWinRate() {
        winRate = ((double)(win) / (double)(20 - draw)) * 100;
        return winRate;
    }

    public Double getCalculatedKda(){
        kda = (((double)kill + (double)assist) / (double)death);
        return kda;
    }
}