package com.ZPO.app.views.list;

import com.ZPO.app.data.DataProcesor;
import com.ZPO.app.data.Match;
import com.ZPO.app.data.Summoner;
import com.ZPO.app.exceptions.NoMatchesException;
import com.ZPO.app.views.MainLayout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ZPO.app.data.Download.*;

@PageTitle("LeagueApp")
@Route(value = "",layout = MainLayout.class)
@CssImport(
        themeFor = "vaadin-grid",
        value = "./styles/styles.css"
)
public class ListView extends VerticalLayout {
    public Summoner summoner = new Summoner();
    public List<Match> matches;
    public Grid<Match> grid = new Grid<>(Match.class);
    public Paragraph summonerName = new Paragraph("");
    public ListView() throws JsonProcessingException {
        setSpacing(false);
        Button button = new Button("Submit");
        TextField id = new TextField("SummonerId");
        TextField tag = new TextField("TagLine");
        HorizontalLayout hl = new HorizontalLayout(id,tag,button);
        hl.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        add(hl);
        configureGrid();
        button.addClickListener(click -> {
            summoner.setSummonerId(id.getValue());
            summoner.setTagLine(tag.getValue());
            addTabs(summoner);
        });

        setSizeFull();
    }
    public void configureGrid() throws JsonProcessingException {

        grid.addClassName("Match");
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(match -> match.getChampion()).setHeader("Champion");
        grid.addColumn(match -> match.getKills()).setHeader("Kills");
        grid.addColumn(match -> match.getDeaths()).setHeader("Deaths");
        grid.addColumn(match -> match.getAssists()).setHeader("Assists");
        grid.addColumn(match -> match.getDuration()).setHeader("Game Duration (min)");
        grid.addColumn(match -> match.getGameMode()).setHeader("Game Mode");
        add(summonerName);
        add(grid);
        ArrayList<String> m= (ArrayList<String>) readJsonListFromFile("src/main/java/com/ZPO/app/Data/db/matchList.json");
        DataProcesor d = new DataProcesor();
        String puuid = readStringFromFile("src/main/java/com/ZPO/app/Data/db/puuid.txt");
        String ids = readStringFromFile("src/main/java/com/ZPO/app/Data/db/ids.json");
        if ((puuid != "") & (ids !="")){
            summonerName.setText("Summoner: "+d.getSumonnerName(m.get(0),puuid));
            matches = d.getPlayerInfoList(m,puuid);
            grid.setItems(matches);
            grid.setClassNameGenerator(match -> match.getWin() ==0 ? "red-background" : "green-background");
        }
    }
    public void removeParagraphs() {
        getChildren().filter(component -> (component instanceof Paragraph))
                .forEach(this::remove);
    }
    public void addTabs(Summoner summoner){

        matches = new ArrayList<>();

        String apiKey = "";
        try (BufferedReader br = new BufferedReader(new FileReader("./apiKey.txt"))) {
            apiKey = br.readLine();
            apiKey = apiKey.replaceAll("\\s", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String puuid = getPuuid(summoner.getSummonerId(), summoner.getTagLine(), apiKey);
            ArrayList<String> ids = getMatchesIds(puuid, 15, apiKey);
            ArrayList<String> info = getMatchInfo(ids, 15, apiKey);
            DataProcesor d = new DataProcesor();
            matches = d.getPlayerInfoList(info,puuid);
            grid.setItems(matches);
            grid.setClassNameGenerator(match -> match.getWin() ==0 ? "red-background" : "green-background");
            summonerName.setText("Summoner: "+d.getSumonnerName(info.get(0),puuid));
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            showNotification("There is no user with such a nickname. Check if you entered the summonerId and TagLine correctly. :)",NotificationVariant.LUMO_WARNING);
        }catch(NoMatchesException e){
            showNotification("This summoner hasn't played any games recently",NotificationVariant.LUMO_WARNING);
        }

    }
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 5000);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
