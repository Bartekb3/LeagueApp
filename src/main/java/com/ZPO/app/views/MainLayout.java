package com.ZPO.app.views;

import com.ZPO.app.views.list.DashboardView;
import com.ZPO.app.views.list.ListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    public MainLayout(){
        createHeader();
        createDrawer();
    }
    private void createHeader(){
        H1 logo = new H1("League of Legends Stats");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(),logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        addToNavbar(header);
    }
    private void createDrawer(){
        RouterLink listView = new RouterLink("Match History", ListView.class);
        listView.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(new VerticalLayout(
                listView,
                new RouterLink("Stats", DashboardView.class)
        ));
    }
}
