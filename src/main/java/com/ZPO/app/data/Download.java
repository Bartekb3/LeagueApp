package com.ZPO.app.data;

import com.ZPO.app.exceptions.NoMatchesException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.gentyref.TypeToken;
import com.nimbusds.jose.shaded.gson.Gson;


import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Download {
    public Download(){}

    public static String apiRequest(String apiKey, String apiUrl) throws IOException, InterruptedException {

            URI uri = URI.create(apiUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("X-Riot-Token", apiKey)
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return(response.body());
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                throw new InterruptedException();

            }

    }
    public static String getPuuid(String summonerId, String tagLine,String apiKey) throws IOException, InterruptedException,IllegalArgumentException {
        String apiUrl = "https://europe.api.riotgames.com/riot/account/v1/accounts/by-riot-id/"+summonerId.replaceAll("\\s","%20")+"/"+tagLine.replaceAll("\\s", "%20");
        String rsp = apiRequest(apiKey,apiUrl);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(rsp).getAsJsonObject();
        String puuid = jsonObject.get("puuid").getAsString();
        writeStringToFile(puuid,"src/main/java/com/ZPO/app/Data/db/puuid.txt");
        return(puuid);
    }

    public static ArrayList<String> getMatchesIds(String puuid, int n, String apiKey) throws IOException, InterruptedException, NoMatchesException {
        ArrayList<String> ids = new ArrayList<>();
        String apiUrl ="https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?start=0&count="+String.valueOf(n) ;
        String rsp = apiRequest(apiKey,apiUrl);
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        ids = gson.fromJson(rsp, listType);
        if(ids.size()==0){

            writeStringToFile(readStringFromFile("src/main/java/com/ZPO/app/Data/db/workingpuuid.txt"),"src/main/java/com/ZPO/app/Data/db/puuid.txt");
            throw new NoMatchesException();
        } else{
           writeStringToFile(puuid,"src/main/java/com/ZPO/app/Data/db/workingpuuid.txt");
        }
        writeJsonListToFile(ids,"src/main/java/com/ZPO/app/Data/db/ids.json");
        return(ids);
    }

    public static ArrayList<String> getMatchInfo(ArrayList<String> ids, int n , String apiKey) throws IOException, InterruptedException {
        ArrayList<String> matchList = new ArrayList<>();
        String apiUrl ="https://europe.api.riotgames.com/lol/match/v5/matches/";
        if(n <= ids.size()){
            Iterator<String> iterator = ids.iterator();
            int c = 0;
            while (c<n) {
                String item = iterator.next();
                matchList.add(apiRequest(apiKey,apiUrl+item));
                c++;
            }
            writeJsonListToFile(matchList,"src/main/java/com/ZPO/app/Data/db/matchList.json");
            return(matchList);
        }else{
            throw new InterruptedException();
        }
    }
    public static ArrayList<String> getMatchTimeline(ArrayList<String> ids, int n , String apiKey) throws IOException, InterruptedException {
        ArrayList<String> matchList = new ArrayList<>();
        String apiUrl ="https://europe.api.riotgames.com/lol/match/v5/matches/";
        if(n <= ids.size()){
            Iterator<String> iterator = ids.iterator();
            int c = 0;
            while (c<n) {
                String item = iterator.next();
                matchList.add(apiRequest(apiKey,apiUrl+item+"/timeline"));
                c++;
            }
            writeJsonListToFile(matchList,"src/main/java/com/ZPO/app/Data/db/matchTimelines.json");
            return(matchList);

        }else{
            throw new InterruptedException();
        }
    }
    public static void writeJsonListToFile(List<String> jsonList, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))) {
            for (String jsonString : jsonList) {
                writer.write(jsonString);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> readJsonListFromFile(String filePath) {
        List<String> jsonList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonList;
    }
    public static void writeStringToFile(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String readStringFromFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString().replaceAll("\\s", "");
    }
}
