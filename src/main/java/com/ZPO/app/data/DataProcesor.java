package com.ZPO.app.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.*;

public class DataProcesor {
    public DataProcesor() {}

    public  Table getPlayerInfo(ArrayList<String> matchInfo, String puuid){

        ObjectMapper objectMapper = new ObjectMapper();
        IntColumn killColumn = IntColumn.create("Kill");
        IntColumn deathColumn = IntColumn.create("Death");
        IntColumn assistColumn = IntColumn.create("Assist");
        IntColumn winColumn = IntColumn.create("Victory");
        StringColumn champColumn = StringColumn.create("Champion");
        IntColumn durationColumn = IntColumn.create("GameDuration");
        StringColumn modeColumn = StringColumn.create("GameMode");
        IntColumn goldColumn = IntColumn.create("Gold");
        IntColumn minionsKilled = IntColumn.create("Minions");

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
                killColumn.append(jsonNode.path("info").path("participants").get(playerIdx).path("kills").asInt());
                deathColumn.append(jsonNode.path("info").path("participants").get(playerIdx).path("deaths").asInt());
                assistColumn.append(jsonNode.path("info").path("participants").get(playerIdx).path("assists").asInt());
                winColumn.append(jsonNode.path("info").path("participants").get(playerIdx).path("win").asInt());
                champColumn.append(jsonNode.path("info").path("participants").get(playerIdx).path("championName").asText());
                durationColumn.append(jsonNode.path("info").path("gameDuration").asInt());
                modeColumn.append(jsonNode.path("info").path("gameMode").asText());
                goldColumn.append(jsonNode.path("info").path("participants").get(playerIdx).path("goldEarned").asInt());
                minionsKilled.append(jsonNode.path("info").path("participants").get(playerIdx).path("totalMinionsKilled").asInt());



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Table table = Table.create("MatchesStats").addColumns(killColumn,deathColumn,assistColumn,winColumn,champColumn,durationColumn,modeColumn,goldColumn);
        return table;
    }
    public List<Match> getPlayerInfoList(ArrayList<String> matchInfo, String puuid){
        List<Match> matches = new ArrayList<>();
        Table tab = getPlayerInfo(matchInfo,puuid);
        for(int i = 0; i < matchInfo.size(); i++){
            String timeString = tab.column("GameDuration").get(i).toString();
            int time = Integer.valueOf(timeString) / 60;
            matches.add(new Match((String)tab.column("Champion").get(i),(int)tab.column("Kill").get(i),
                    (int)tab.column("Death").get(i),(int)tab.column("Assist").get(i),time,
                    (int)tab.column("Victory").get(i),tab.column("GameMode").get(i).toString()));
        }
        return(matches);
    }

    public double[] calculateStats(ArrayList<String> matchInfo,Table tab){
        int winCounter=0;
        int killCounter=0;
        int deathCounter=0;
        int assistCounter=0;
        int goldCounter=0;
        for(int i = 0; i < matchInfo.size(); i++){
            String victoryValue = tab.column("Victory").get(i).toString();
            String killValue = tab.column("Kill").get(i).toString();
            String deathValue = tab.column("Death").get(i).toString();
            String assistValue = tab.column("Assist").get(i).toString();
            String goldValue = tab.column("Gold").get(i).toString();

            if (!victoryValue.isEmpty()) {
                winCounter += Integer.valueOf(victoryValue);
            }

            if (!killValue.isEmpty()) {
                killCounter += Integer.valueOf(killValue);
            }

            if (!deathValue.isEmpty()) {
                deathCounter += Integer.valueOf(deathValue);
            }

            if (!assistValue.isEmpty()) {
                assistCounter += Integer.valueOf(assistValue);
            }
            if (!goldValue.isEmpty()) {
                goldCounter += Integer.valueOf(goldValue);
            }
        }
        double winrate = (((double) winCounter * 100) / (matchInfo.size()));
        double kda = ((double) killCounter + (double) assistCounter) / (double) deathCounter;
        double gold = ((double) goldCounter ) / (double) (matchInfo.size());

        //returns array [winrate,kda,gold]
        return(new double[]{winrate, kda, gold});
    }


    public String getSumonnerName(String match,String puuid) throws JsonProcessingException {
        int playerIdx = -1;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(match);
        JsonNode participantsNode = jsonNode.path("metadata").path("participants");
        for (int i = 0; i < participantsNode.size(); i++) {
            if (puuid.equals(participantsNode.get(i).asText())) {
                playerIdx = i;
                break;
            }
        }
        return(jsonNode.path("info").path("participants").get(playerIdx).path("summonerName").asText());
    }


}

