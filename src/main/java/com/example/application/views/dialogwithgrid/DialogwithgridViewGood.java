package com.example.application.views.dialogwithgrid;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.collaborationengine.CollaborationMessageInput;
import com.vaadin.collaborationengine.CollaborationMessageList;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.example.application.views.MainLayout;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

@PageTitle("dialogwithgrid-good")
@Route(value = "good", layout = MainLayout.class)
@RouteAlias(value = "good", layout = MainLayout.class)
public class DialogwithgridViewGood extends VerticalLayout {

    private static final List<GridRow> GRID_ROWS = new LinkedList<>();

    public DialogwithgridViewGood() {
        addClassName("dialogwithgrid-view");
        setSpacing(false);

        // Layouting
        add(createButtonWithDialog());
        setSizeFull();
    }


    private Component createButtonWithDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("1300px");
        dialog.setHeight("700px");
        dialog.setModal(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);

        Grid<GridRow> grid = createGrid();
        Button closeButton = new Button("Close", event -> dialog.close());

        dialog.add(grid, closeButton);


        Button openButton = new Button("Press to open Dialog");
        openButton.addClickListener(event -> {
            Executors.newSingleThreadExecutor().submit(() -> {
                openButton.getUI().ifPresent(ui -> {
                    ui.access(() -> {
                        refreshData();
                        randomSelectRow(grid);
                        grid.getDataProvider().refreshAll();
                    });
                });
            });


            if (!dialog.isOpened()) {
                dialog.open();
            }

        });


        return openButton;
    }

    private void refreshData() {
        List<GridRow> newList = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            GridRow gridRow = new GridRow(i,
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000),
                    RandomStringUtils.randomAlphanumeric(1000));
            newList.add(gridRow);
        }
        GRID_ROWS.clear();
        GRID_ROWS.addAll(newList);
    }


    private void randomSelectRow(Grid<GridRow> grid) {
        int index = new Random().nextInt(GRID_ROWS.size());
        grid.scrollToIndex(index);

        // Creating copy of item to ensure that it doesn't exist
        GridRow g = GRID_ROWS.get(index);
        GridRow gridRow = new GridRow(g.id, g.col01, g.col02, g.col03, g.col04, g.col05, g.col06, g.col07, g.col08, g.col09, g.col10);
        grid.select(gridRow);
        Notification.show("ID=" + gridRow.getId());
    }


    private Grid<GridRow> createGrid() {
        Grid<GridRow> grid = new Grid<>(GridRow.class);
        grid.setPageSize(10);
        grid.setWidthFull();
        grid.setHeight("90%");
        grid.setSortableColumns();

        CallbackDataProvider<GridRow, Void> dataProvider1 = new CallbackDataProvider<>(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    List<GridRow> collect = GRID_ROWS.stream().skip(offset).limit(limit).collect(Collectors.toList());
                    Notification.show("Offset=" + offset + " Limit=" + limit + " Fetched=" + collect.size());
                    return collect.stream();
                },
                query -> GRID_ROWS.size(),
                gridRow -> gridRow.getId()); // WARNING. Unuseful. Didn't work

        grid.setDataProvider(dataProvider1);

        return grid;
    }


    /**
     * With equals() and hashCode() overrided Grid can select by item
     */
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GridRow {
        private int id;
        private String col01;
        private String col02;
        private String col03;
        private String col04;
        private String col05;
        private String col06;
        private String col07;
        private String col08;
        private String col09;
        private String col10;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridRow gridRow = (GridRow) o;
            return id == gridRow.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }


}