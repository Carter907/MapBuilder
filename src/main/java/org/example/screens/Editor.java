package org.example.screens;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.AppStart;
import org.example.edit.Cell;
import org.example.util.MapManager;
import org.example.util.Vector2D;

import java.io.File;
import java.util.Optional;

public class Editor extends BorderPane {

    private BooleanProperty beeperSelected;
    private BooleanProperty verticalSelected;
    private BooleanProperty horizontalSelected;
    private BooleanProperty removalSelected;
    private BooleanProperty freeClickSelected;
    private ObservableList<BooleanProperty> selections;
    private AppStart appStart;

    private int width;
    private int height;

    public Editor(String map, AppStart appStart) {

        this.appStart = appStart;
    }

    public Editor(AppStart appStart, int width, int height) {


        this.width = width;
        this.height = height;


        this.appStart = appStart;
        freeClickSelected = new SimpleBooleanProperty(false);
        beeperSelected = new SimpleBooleanProperty(false);
        verticalSelected = new SimpleBooleanProperty(false);
        horizontalSelected = new SimpleBooleanProperty(false);
        removalSelected = new SimpleBooleanProperty(false);


        selections = FXCollections.observableArrayList();
        selections.addAll(freeClickSelected, beeperSelected, verticalSelected, horizontalSelected, removalSelected);

        for (BooleanProperty bool : selections)
            bool.addListener(this::selectionChange);

        setPadding(new Insets(10));
        setBackground(new Background(new BackgroundImage(new Image("assets/EditorBackground.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        setRight(initializeRight());
        setTop(initializeTop());
        update();

        System.out.println("\nEditor Set: width=" + width + ", height=" + height);
        appStart.getNodeManager().give("editor", this);

    }

    private Pane initializeTop() {
        Pane parent = new Pane();

        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");

        MapManager mapper = new MapManager(this);
        MenuItem startNewFile = new MenuItem("New");

        TextInputDialog sizeRequest = new TextInputDialog();
        ImageView icon = new ImageView("assets/Karel_Icon.png");
        icon.setSmooth(true);
        icon.setOpacity(0.7);
        sizeRequest.setGraphic(icon);
        sizeRequest.getDialogPane().setMaxWidth(250);
        sizeRequest.setHeaderText("Please enter the width of your world");
        sizeRequest.setTitle("Karel Map Builder");
        sizeRequest.getEditor().setMaxWidth(100);


        Vector2D worldSize = new Vector2D();
        startNewFile.setOnAction(e -> {

            Optional<String> result = sizeRequest.showAndWait();
            if (!result.isPresent())
                return;
            else
                worldSize.setX(Integer.parseInt(result.get()));
            sizeRequest.getEditor().clear();
            sizeRequest.setHeaderText("Please enter the height of your world");
            result = sizeRequest.showAndWait();
            if (!result.isPresent())
                return;
            else
                worldSize.setY(Integer.parseInt(result.get()));
            setEditorWidth(worldSize.getX());
            setEditorHeight(worldSize.getY());
            update();
        });
        MenuItem openFile = new MenuItem("Open...");
        openFile.setOnAction(e -> {
            File fileChosen = mapper.getOpenFile();
            if (fileChosen == null)
                return;
            mapper.clearLists();
            mapper.loadMap(fileChosen);
            update();
            mapper.setMapObjects(parseGrid((GridPane) appStart.getNodeManager().retrieve("cells")));


        });
        MenuItem saveAs = new MenuItem("Save As...");

        saveAs.setOnAction(e -> {
            File fileChosen = mapper.getSaveFile();
            if (fileChosen == null)
                return;
            mapper.constructMap(parseGrid((GridPane) appStart.getNodeManager().retrieve("cells")));
            mapper.saveMap(fileChosen);
        });

        MenuItem saveFile = new MenuItem("Save");

        saveFile.setOnAction(e -> {

            if (mapper.getCurrentFile() == null)
                return;
            mapper.constructMap(parseGrid((GridPane) appStart.getNodeManager().retrieve("cells")));
            mapper.saveMap(mapper.getCurrentFile());
        });

        file.getItems().

                addAll(startNewFile, openFile, saveFile, saveAs);
        menuBar.getMenus().

                add(file);
        parent.getChildren().

                add(menuBar);


        return parent;
    }

    private Cell[][] parseGrid(GridPane grid) {

        Cell[][] cells = new Cell[height][width];
        System.out.println("height: " + height);
        System.out.println("width: " + width);

        for (Node n : grid.getChildren()) {
            if (n instanceof Cell)
                cells[GridPane.getRowIndex(n)][GridPane.getColumnIndex(n)] = (Cell) n;
        }
        return cells;
    }


    private Pane initializeRight() {

        Pane parent = new Pane();
        ListView<Label> selectionView = new ListView<>();
        selectionView.setPrefHeight(520);
        selectionView.setMinWidth(200);
        selectionView.setFixedCellSize(100);

        Label beeperSelection = new Label("Beeper", new ImageView("assets/Beeper.png"));
        selectionView.getItems().add(beeperSelection);
        beeperSelection.setAlignment(Pos.CENTER);


        Label horizontalSelection = new Label("Horizontal", new ImageView("assets/Horizontal.png"));
        selectionView.getItems().add(horizontalSelection);
        horizontalSelection.setAlignment(Pos.CENTER);

        Label verticalSelection = new Label("Vertical", new ImageView("assets/Vertical.png"));
        selectionView.getItems().add(verticalSelection);
        verticalSelection.setAlignment(Pos.CENTER);

        Label removeSelection = new Label("Remove", new ImageView("assets/Remove.png"));
        selectionView.getItems().add(removeSelection);
        removeSelection.setAlignment(Pos.CENTER);

        Label freeClickSelection = new Label("Free Click", new ImageView("assets/Free_Click.png"));
        selectionView.getItems().add(freeClickSelection);
        freeClickSelection.setAlignment(Pos.CENTER);

        selectionView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        selectionView.setOnMouseClicked(this::checkSelection);
        parent.getChildren().add(selectionView);

        return parent;
    }

    private void selectionChange(ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue) {

        if (newValue)
            if (o.equals(freeClickSelected)) {
                for (BooleanProperty bool : selections) {
                    bool.set(true);
                }
            } else if (!freeClickSelected.get()) {
                for (BooleanProperty bool : selections) {
                    if (!bool.equals(o))
                        bool.set(false);
                }
            }
    }


    private void checkSelection(MouseEvent e) {
        if (!(e.getSource() instanceof ListView))
            return;
        System.out.println("selected");

        Label selected = ((ListView<Label>) e.getSource()).getSelectionModel().getSelectedItem();
        System.out.println(selected.getText());

        freeClickSelected.set(selected.getText().equals("Free Click"));
        if (!freeClickSelected.get()) {
            beeperSelected.set(selected.getText().equals("Beeper"));
            horizontalSelected.set(selected.getText().equals("Horizontal"));
            verticalSelected.set(selected.getText().equals("Vertical"));
            removalSelected.set(selected.getText().equals("Remove"));
        }
    }

    public void update() {
        Cell.cellSize = (int) (450 * (1 / (double) width));
        setCenter(initializeCenter());


    }

    private GridPane initializeCenter() {

        GridPane cells = new GridPane();
        cells.setBackground(new Background(
                new BackgroundFill(
                        Color.web("#6f9ccf", .5),
                        new CornerRadii(50),
                        new Insets(0, 100, 0, 100))));
        cells.setGridLinesVisible(false);
        Cell sizer = new Cell(true, true, true, true, this, -1, -1);

//        totalWidth = (inner.getHeight() / Cell.Outer.size) / Math.tan(Math.toRadians(45))*4+Cell.this.cellSize;
//        totalHeight =(inner.getWidth() / Cell.Outer.size) * Math.tan(Math.toRadians(45))*4+Cell.this.cellSize;

        cells.setHgap((sizer.getTotalWidth() / 8) - sizer.getCellSize());
        cells.setVgap((sizer.getTotalHeight() / 8) - sizer.getCellSize());
        cells.setAlignment(Pos.CENTER);


        setEditor(cells);
        appStart.getNodeManager().give("cells", cells);
        return cells;
    }

    private void setEditor(GridPane cells) {
        for (int avenue = 0; avenue < height; avenue++)
            for (int street = 0; street < width; street++) {
                if (avenue % 2 == 0)
                    if (street == 0)
                        cells.add(new Cell(true, true, true, true, this, avenue, street), street, avenue);
                    else
                        cells.add(new Cell(true, false, true, true, this, avenue, street), street, avenue);

                else if (avenue == height - 1) {

                    if (street == 0)
                        cells.add(new Cell(false, true, true, true, this, avenue, street), street, avenue);
                    else
                        cells.add(new Cell(false, false, true, true, this, avenue, street), street, avenue);
                } else if (street == 0)
                    cells.add(new Cell(false, true, true, false, this, avenue, street), street, avenue);
                else
                    cells.add(new Cell(false, false, true, false, this, avenue, street), street, avenue);
            }
    }


    public int getEditorWidth() {
        return width;
    }


    public int getEditorHeight() {
        return height;
    }

    public void setEditorWidth(int width) {
        this.width = width;
    }

    public void setEditorHeight(int height) {
        this.height = height;
    }

    public AppStart getAppStart() {
        return appStart;
    }

    public boolean isBeeperSelected() {
        return beeperSelected.get();
    }

    public boolean isVerticalSelected() {
        return verticalSelected.get();
    }

    public boolean isFreeClickSelected() {
        return freeClickSelected.get();
    }

    public BooleanProperty freeClickSelectedProperty() {
        return freeClickSelected;
    }

    public boolean isHorizontalSelected() {
        return horizontalSelected.get();
    }

    public boolean isRemoveSelected() {
        return removalSelected.get();
    }

}
