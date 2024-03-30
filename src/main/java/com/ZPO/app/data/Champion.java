package com.ZPO.app.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Champion {

    private String champion;
    private int kills;
    private double deaths;
    private int assists;
    private double sum_time;
    private int wins;
    private int minions;
    private double matches;
    private int gold;
    private String last_match_id;
    private String kda;
    private String minionsPerMin;
    private String pickrate;
    private String goldPerGame;
    private String winRate;
    public static double total_matches;


    public String getKda() {
        return kda;
    }

    public String getMinionsPerMin() {
        return minionsPerMin;
    }

    public String getPickrate() {
        return pickrate;
    }

    public String getGoldPerGame() {
        return goldPerGame;
    }

    public String getWinRate() {
        return winRate;
    }

    public String getChampionName() {
        return champion;
    }

    public Champion(String champion){
        this.champion = champion;
    }

    public void updateStats(int kills,double deaths,int assists,double sum_time,double wins,int minions,int gold,String last_match_id){
        this.kills += kills;
        this.deaths += deaths;
        this.assists += assists;
        this.sum_time += sum_time;
        this.wins += wins;
        this.minions += minions;
        this.matches += 1;
        this.gold += gold;
        this.last_match_id = last_match_id;
    }


    public void summarizeStats(){
        if (matches!=0) {
            this.kda = String.format("%.2f", (deaths == 0) ? (kills + assists) : (kills + assists) / deaths);
            this.minionsPerMin = String.format("%.2f", minions / sum_time);
            this.pickrate = String.format("%.2f", matches / total_matches);
            this.winRate = String.format("%.2f", wins / matches);
            this.goldPerGame = String.format("%.2f", gold / total_matches);

        }
    }
}
