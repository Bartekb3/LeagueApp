package com.ZPO.app.views.list;

import com.ZPO.app.data.Champion;
import com.ZPO.app.data.DataProcesor;
import com.ZPO.app.data.Match;
import com.ZPO.app.data.Summoner;
import com.ZPO.app.views.MainLayout;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;

import javax.swing.*;
import javax.xml.transform.stream.StreamSource;
import java.util.*;

import static com.ZPO.app.data.Download.readJsonListFromFile;
import static com.ZPO.app.data.Download.readStringFromFile;

@Route(value = "dashboard",layout = MainLayout.class)
@PageTitle("Statistics")
public class DashboardView extends VerticalLayout {
    public Grid<Champion> grid2 = new Grid<>(Champion.class);
    public DashboardView(){
        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        addTabs();
    }
    public void removeParagraphs() {
        getChildren().filter(component -> (component instanceof Paragraph))
                .forEach(this::remove);
    }
    public void addTabs(){
        removeParagraphs();
        ArrayList<String> m= (ArrayList<String>) readJsonListFromFile("src/main/java/com/ZPO/app/Data/db/matchList.json");
        DataProcesor d = new DataProcesor();
        String puuid = readStringFromFile("src/main/java/com/ZPO/app/Data/db/puuid.txt");
        String ids = readStringFromFile("src/main/java/com/ZPO/app/Data/db/ids.json");
        if ((puuid != "") & (ids !="")) {
            double[] t = d.calculateStats(m,d.getPlayerInfo(m,puuid));
            double winrate = t[0];
            double kda = t[1];
            double gold = t[2];
            Paragraph p1 = new Paragraph("Winrate: " + winrate + "%");
            Paragraph p2 = new Paragraph("KDA: " + kda);
            Paragraph p3 = new Paragraph("Mean gold per game: " + gold);
            p3.getStyle().set("background-color", "gold");

            if (winrate >= 50) {
                p1.getStyle().set("background-color", "lightgreen");

            } else {
                p1.getStyle().set("background-color", "#FFCCCC");
            }
            add(p1);
            if (kda <= 2.0) {
                p2.getStyle().set("background-color", "#FFCCCC");
            } else if (kda > 2.0 & kda <= 5.0) {
                p2.getStyle().set("background-color", "#FFFF00");
            } else {
                p2.getStyle().set("background-color", "lightgreen");
            }
            add(p2);
            add(p3);

            grid2.addClassName("Champion");
            grid2.setColumns();
            grid2.addColumn(champion -> champion.getChampionName()).setHeader("Champion Name");
            grid2.addColumn(champion -> champion.getKda()).setHeader("Kda");
            grid2.addColumn(champion -> champion.getGoldPerGame()).setHeader("Gold Per Game");
            grid2.addColumn(champion -> champion.getPickrate()).setHeader("Champion Pickrate");
            grid2.addColumn(champion -> champion.getMinionsPerMin()).setHeader("Minions Per Minute");
            grid2.addColumn(champion -> champion.getWinRate()).setHeader("Win Rate");
            add(new Paragraph("Szczegółowe statystyki dla każdego championa"));
            add(grid2);

            HashMap<String, Champion> hashMap = ChampionStats(m,puuid);
            List<Champion> championList = new ArrayList<>(hashMap.values());
            championList.forEach(Champion::summarizeStats);
            grid2.setItems(championList);


        }
    }

    public HashMap<String, Champion> ChampionStats(ArrayList<String> matchInfo, String puuid){
        HashMap<String,Champion> hashMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        int kills;
        int deaths;
        int assists;
        double sum_time;
        int wins;
        int minions;
        int gold;
        String last_match_id;
        String champion;

        Champion.total_matches = matchInfo.size();


        Iterator<String> iter = matchInfo.iterator();

        while (iter.hasNext()) {
            String item = iter.next();

            try {
                int playerIdx = -1;
                JsonNode jsonNode = objectMapper.readTree(item);
                JsonNode participantsNode = jsonNode.path("metadata").path("participants");
                for (int i = 0; i < participantsNode.size(); i++) {
                    if (puuid.equals(participantsNode.get(i).asText())) {
                        playerIdx = i;
                        break;
                    }
                }

                champion = (jsonNode.path("info").path("participants").get(playerIdx).path("championName").asText());
                if (hashMap.containsKey(champion)) {

                    kills = (jsonNode.path("info").path("participants").get(playerIdx).path("kills").asInt());
                    deaths = (jsonNode.path("info").path("participants").get(playerIdx).path("deaths").asInt());
                    assists = (jsonNode.path("info").path("participants").get(playerIdx).path("assists").asInt());
                    wins = (jsonNode.path("info").path("participants").get(playerIdx).path("win").asInt());
                    sum_time = (jsonNode.path("info").path("gameDuration").asDouble())/60;
                    gold = (jsonNode.path("info").path("participants").get(playerIdx).path("goldEarned").asInt());
                    minions = (jsonNode.path("info").path("participants").get(playerIdx).path("totalMinionsKilled").asInt());
                    last_match_id = (jsonNode.path("metadata").path("matchId").asText());

                    Champion tempChamp = hashMap.get(champion);
                    tempChamp.updateStats(kills,deaths,assists,sum_time,wins,minions,gold,last_match_id);
                } else {
                    Champion tempChamp = new Champion(champion);
                    kills = (jsonNode.path("info").path("participants").get(playerIdx).path("kills").asInt());
                    deaths = (jsonNode.path("info").path("participants").get(playerIdx).path("deaths").asInt());
                    assists = (jsonNode.path("info").path("participants").get(playerIdx).path("assists").asInt());
                    wins = (jsonNode.path("info").path("participants").get(playerIdx).path("win").asInt());
                    sum_time = (jsonNode.path("info").path("gameDuration").asDouble())/60;
                    gold = (jsonNode.path("info").path("participants").get(playerIdx).path("goldEarned").asInt());
                    minions = (jsonNode.path("info").path("participants").get(playerIdx).path("totalMinionsKilled").asInt());
                    last_match_id = (jsonNode.path("metadata").path("matchId").asText());

                    tempChamp.updateStats(kills,deaths,assists,sum_time,wins,minions,gold,last_match_id);
                    hashMap.put(champion,tempChamp);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }
    }

