package com.ZPO.app.data;

public class Match {
    private String champion;
    private String gameMode;
    private int kills;
    private int deaths;
    private int assists;
    private int duration;
    private int win;

    public Match(String champion,int kills, int deaths, int assists, int duration, int win,String gameMode) {
        this.gameMode = gameMode;
        this.champion = champion;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.duration = duration;
        this.win = win;
    }

    public String getChampion() {
        return champion;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
}
