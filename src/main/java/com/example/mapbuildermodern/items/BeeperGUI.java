package com.example.mapbuildermodern.items;

import com.example.mapbuildermodern.AppStart;
import com.example.mapbuildermodern.edit.Cell;
import com.example.mapbuildermodern.items.Beeper;
import com.example.mapbuildermodern.screens.Editor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;


public class BeeperGUI extends Group {

    private IntegerProperty countProperty;
    private Text countText;
    private ImageView beeperIcon;

    private Beeper beeperBacking;
    private Cell cell;
    private Editor editor;

    public BeeperGUI(int count, Editor editor, Cell cell) {

        this.editor = editor;
        this.cell = cell;
        this.countProperty = new SimpleIntegerProperty(count);
        this.beeperBacking = new Beeper(cell.getColIndex(), cell.getRowIndex(), countProperty().get());

        beeperIcon = new ImageView(AppStart.class.getResource("assets/Beeper.png").toExternalForm());
        initializeIcon(beeperIcon);

        countText = new Text();

        initializeCount(countText);

        countProperty().addListener(this::checkBeepers);

        this.setVisible(false);
        getChildren().addAll(beeperIcon, countText);
    }


    private void checkBeepers(ObservableValue<? extends Number> e, Number oldValue, Number newValue) {
        boolean check = newValue.intValue() >= 1;
        System.out.println("new value: " + newValue.intValue() + "\n visible: " + check);
        countText.setText(newValue.intValue() + "");
        beeperBacking.setNum(newValue.intValue());
        this.setVisible(check);
    }

    private void initializeCount(Text countText) {

        countText.setScaleX(Cell.cellSize/45d);
        countText.setScaleY(Cell.cellSize/45d);


        countText.setTextAlignment(TextAlignment.LEFT);
        countText.setFill(Color.WHITE);
        countText.setTextOrigin(VPos.CENTER);
        countText.setFont(Font.font(15));
        countText.setTranslateX(-(countText.getWrappingWidth())/2d-4);
        countText.setX(Cell.cellSize/2d);
        countText.setY(Cell.cellSize/2d);
        countText.setDisable(true);

    }

    private void initializeIcon(ImageView beeperIcon) {
        beeperIcon.setScaleX(Cell.cellSize/45d);
        beeperIcon.setScaleY(Cell.cellSize/45d);
        beeperIcon.setViewport(new Rectangle2D(0, 0, beeperIcon.getImage().getWidth(), beeperIcon.getImage().getHeight()));
        beeperIcon.setTranslateX(-(beeperIcon.getImage().getWidth())/2d);
        beeperIcon.setTranslateY(-(beeperIcon.getImage().getHeight())/2d);
        beeperIcon.setX(Cell.cellSize/2d);
        beeperIcon.setY(Cell.cellSize/2d);

        beeperIcon.setDisable(true);
    }

    public Beeper getBeeperBacking() {
        return beeperBacking;
    }

    public int getCount() {
        return countProperty.get();
    }

    public IntegerProperty countProperty() {
        return countProperty;
    }
}
