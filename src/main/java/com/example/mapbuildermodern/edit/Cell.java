package com.example.mapbuildermodern.edit;


import com.example.mapbuildermodern.items.BeeperGUI;
import com.example.mapbuildermodern.items.Wall;
import com.example.mapbuildermodern.screens.Editor;
import com.example.mapbuildermodern.util.NodeManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Cell extends Group {
    public static int cellSize;
    private final NodeManager cellManager = new NodeManager();
    private ArrayList<Outer> cellWalls;
    private Editor editor;
    private double totalWidth;
    private double totalHeight;
    private int rowIndex;
    private int colIndex;
    private IntegerProperty beeperProperty;
    private BeeperGUI beeperGUIManager;

    public Cell(boolean top, boolean left, boolean right, boolean bot, Editor editor, int rowIndex, int colIndex) {


        this.rowIndex = rowIndex;
        this.colIndex = colIndex;

        this.editor = editor;
        beeperProperty = new SimpleIntegerProperty(0);
        Rectangle inner = new Rectangle(cellSize, cellSize);
        inner.setStroke(Color.BLACK);
        inner.setFill(Color.WHITE);
        inner.setOnMouseEntered(this::mouseEntered);
        inner.setOnMouseExited(this::mouseExited);
        inner.setOnMouseClicked(this::mouseClicked);

        cellManager.give("inner", inner);
        getChildren().add(inner);

        Outer outerBot, outerLeft, outerTop, outerRight;

        outerTop = new Outer(inner, Pos.TOP_CENTER, true);
        cellManager.give("top", outerTop);
        if (!top) {
            outerTop.setUnusable(true);
            outerTop.setFill(Color.TRANSPARENT);
            outerTop.setStroke(Color.TRANSPARENT);
        }

        outerLeft = new Outer(inner, Pos.CENTER_LEFT, true);
        cellManager.give("left", outerLeft);
        if (!left) {
            outerLeft.setUnusable(true);
            outerLeft.setFill(Color.TRANSPARENT);
            outerLeft.setStroke(Color.TRANSPARENT);
        }


        outerRight = new Outer(inner, Pos.CENTER_RIGHT, true);
        cellManager.give("right", outerRight);
        if (!right) {
            outerRight.setUnusable(true);
            outerRight.setFill(Color.TRANSPARENT);
            outerRight.setStroke(Color.TRANSPARENT);
        }

        outerBot = new Outer(inner, Pos.BOTTOM_CENTER, true);

        cellManager.give("bot", outerBot);

        if (!bot) {
            outerBot.setUnusable(true);
            outerBot.setFill(Color.TRANSPARENT);
            outerBot.setStroke(Color.TRANSPARENT);
        }
        cellWalls = new ArrayList<>();
        cellWalls.add(outerBot);
        cellWalls.add(outerLeft);
        cellWalls.add(outerTop);
        cellWalls.add(outerRight);

        totalWidth = (inner.getHeight() / Outer.size) / Math.tan(Math.toRadians(45)) * 4 + Cell.this.cellSize;
        totalHeight = (inner.getWidth() / Outer.size) * Math.tan(Math.toRadians(45)) * 4 + Cell.this.cellSize;

        beeperGUIManager = new BeeperGUI(beeperProperty.get(), editor, this);

        beeperGUIManager.countProperty().bind(beeperProperty);


        getChildren().addAll(outerBot, outerLeft, outerRight, outerTop, beeperGUIManager);


    }

    public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof Outer) {
            Outer outer = (Outer) e.getSource();
            if (!outer.isWall()) {
                outer.setFill(Color.LIGHTGRAY);
            }
        } else if (e.getSource() instanceof Rectangle)
            ((Rectangle) e.getSource()).setFill(Color.LIGHTGRAY);
    }

    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof Outer) {
            Outer outer = (Outer) e.getSource();
            if (!outer.isWall()) {
                outer.setFill(Color.WHITE);
            }
        } else if (e.getSource() instanceof Rectangle)
            ((Rectangle) e.getSource()).setFill(Color.WHITE);
    }

    public void mouseClicked(MouseEvent e) {

        Node source = (Node) e.getSource();
        GridPane cells = (GridPane) editor.getAppStart().getNodeManager().retrieve("cells");
        Cell parentCell = (Cell) source.getParent();
        int index = cells.getChildren().indexOf(parentCell);

        if (e.getSource() instanceof Outer) {
            Outer outer = (Outer) source;
            if (!outer.isWall()) {

                if (editor.isHorizontalSelected() && outer.isHorizontal()) {
                    outer.setWall(true);
                } else if (editor.isVerticalSelected() && outer.isVertical()) {
                    outer.setWall(true);
                }
            } else if (editor.isRemoveSelected()) {
                outer.setWall(false);
            }


        } else if (e.getSource() instanceof Rectangle) {
            Rectangle inner = (Rectangle) source;
            if (editor.isBeeperSelected()) {
                parentCell.setBeepers(parentCell.getBeepers() + 1);
                System.out.println(parentCell.getBeepers());

            } else if (editor.isRemoveSelected() && parentCell.getBeepers() > 0) {
                parentCell.setBeepers(parentCell.getBeepers() - 1);
            }

        }

    }


    public class Outer extends Path {
        private static final double size = 3d;
        private boolean big;
        private double extendHeight;
        private double extendWidth;
        private boolean wall;
        private boolean vertical;
        private boolean horizontal;
        private Pos orientation;
        private Wall wallBacking;

        private boolean unusable;
        private boolean beginning;

        public Outer(Rectangle origin, Pos orientation, boolean big) {

            extendWidth = (origin.getHeight() / size) / Math.tan(Math.toRadians(45));
            extendHeight = (origin.getWidth() / size) * Math.tan(Math.toRadians(45));
            unusable = false;
            this.beginning = false;
            this.wall = false;
            this.big = big;
            this.orientation = orientation;
            wallBacking = new Wall(Cell.this.colIndex, Cell.this.rowIndex);

            if (wallBacking.getX() != 0 && (orientation != Pos.BOTTOM_CENTER && orientation != Pos.TOP_CENTER)) {

                wallBacking.setX(wallBacking.getX()+1);

            }


            switch (orientation) {
                case TOP_CENTER:
                    wallBacking.setStyle(Wall.WallStyle.HORIZONTAL);
                    vertical = false;
                    horizontal = true;
                    if (big)
                        this.getElements().addAll(
                                new MoveTo(-extendWidth, -origin.getHeight() / size),
                                new LineTo(origin.getX(), origin.getY()),
                                new LineTo(origin.getWidth(), origin.getY()),
                                new LineTo(origin.getWidth() + extendWidth, -origin.getHeight() / size),
                                new LineTo(origin.getWidth(), 2 * -origin.getHeight() / size),
                                new LineTo(origin.getX(), 2 * -origin.getHeight() / size),
                                new LineTo(-extendWidth, -origin.getHeight() / size)
                        );
                    else
                        this.getElements().addAll(
                                new MoveTo(-extendWidth, -origin.getWidth() / size),
                                new LineTo(origin.getX(), origin.getY()),
                                new LineTo(origin.getWidth(), origin.getY()),
                                new LineTo(origin.getWidth() + origin.getWidth() / size, -extendHeight)
                        );
                    break;
                case CENTER_LEFT:
                    wallBacking.setStyle(Wall.WallStyle.VERTICAL);
                    vertical = true;
                    horizontal = false;

                    if (big)
                        this.getElements().addAll(
                                new MoveTo(-extendWidth, -origin.getHeight() / size),
                                new LineTo(origin.getX(), origin.getY()),
                                new LineTo(origin.getX(), origin.getHeight()),
                                new LineTo(-extendWidth, origin.getHeight() + origin.getHeight() / size),
                                new LineTo(2 * -extendWidth, origin.getHeight()),
                                new LineTo(2 * -extendWidth, origin.getY()),
                                new LineTo(-extendWidth, -origin.getHeight() / size)
                        );
                    else
                        this.getElements().addAll(
                                new MoveTo(-extendWidth, -origin.getWidth() / size),
                                new LineTo(origin.getX(), origin.getY()),
                                new LineTo(origin.getX(), origin.getHeight()),
                                new LineTo(-extendWidth, origin.getHeight() + origin.getHeight() / size)
                        );

                    break;

                case CENTER_RIGHT:
                    wallBacking.setStyle(Wall.WallStyle.VERTICAL);
                    horizontal = false;
                    if (wallBacking.getX() == 0)
                        wallBacking.setX(1);

                    vertical = true;
                    if (big)
                        this.getElements().addAll(
                                new MoveTo(origin.getWidth() + extendWidth, -origin.getHeight() / size),
                                new LineTo(origin.getWidth(), origin.getY()),
                                new LineTo(origin.getWidth(), origin.getHeight()),
                                new LineTo(origin.getWidth() + extendWidth, origin.getHeight() + origin.getHeight() / size),
                                new LineTo(origin.getWidth() + 2 * extendWidth, origin.getHeight()),
                                new LineTo(origin.getWidth() + 2 * extendWidth, origin.getY()),
                                new LineTo(origin.getWidth() + extendWidth, -origin.getHeight() / size)

                        );
                    else
                        this.getElements().addAll(
                                new MoveTo(origin.getWidth() + extendWidth, -origin.getHeight() / size),
                                new LineTo(origin.getWidth(), origin.getY()),
                                new LineTo(origin.getWidth(), origin.getHeight()),
                                new LineTo(origin.getWidth() + extendWidth, origin.getHeight() + origin.getHeight() / size)

                        );

                    break;

                case BOTTOM_CENTER:
                    wallBacking.setStyle(Wall.WallStyle.HORIZONTAL);
                    horizontal = true;
                    vertical = false;
                    if (wallBacking.getY() % 2 == 0 || wallBacking.getY() == editor.getEditorHeight()-1)
                        wallBacking.setY(wallBacking.getY()+1);
                    if (big)
                        this.getElements().addAll(
                                new MoveTo(origin.getWidth() + extendWidth, origin.getHeight() + origin.getHeight() / size),
                                new LineTo(origin.getWidth(), origin.getHeight()),
                                new LineTo(origin.getX(), origin.getHeight()),
                                new LineTo(-extendWidth, origin.getHeight() + origin.getHeight() / size),
                                new LineTo(origin.getX(), origin.getHeight() + 2 * origin.getHeight() / size),
                                new LineTo(origin.getWidth(), origin.getHeight() + 2 * origin.getHeight() / size),
                                new LineTo(origin.getWidth() + extendWidth, origin.getHeight() + origin.getHeight() / size)

                        );
                    else
                        this.getElements().addAll(
                                new MoveTo(origin.getWidth() + extendWidth, origin.getHeight() + origin.getHeight() / size),
                                new LineTo(origin.getWidth(), origin.getHeight()),
                                new LineTo(origin.getX(), origin.getHeight()),
                                new LineTo(-extendWidth, origin.getHeight() + origin.getHeight() / size)
                        );
                    break;

            }
            setFill(Color.WHITE);
            setOnMouseEntered(Cell.this::mouseEntered);
            setOnMouseExited(Cell.this::mouseExited);
            setOnMouseClicked(Cell.this::mouseClicked);

        }

        public Pos getOrientation() {
            return orientation;
        }

        public Wall getWallBacking() {
            return wallBacking;
        }

        public boolean isVertical() {
            return vertical;
        }

        public boolean isHorizontal() {
            return horizontal;
        }

        public boolean isWall() {
            return wall;
        }

        public void setWall(boolean wall) {
            if (!unusable) {
                this.wall = wall;
                setFill(wall ? Color.BLACK : Color.WHITE);

            }
        }

        public void setUnusable(boolean value) {
            this.setDisable(value);
            this.setWall(!value);
            unusable = value;
            wallBacking = value ? null : wallBacking;
        }
        public boolean isUnusable() {
            return unusable;
        }
    }

    private void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    private void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public NodeManager getCellManager() {
        return cellManager;
    }

    public double getTotalWidth() {
        return totalWidth;
    }

    public double getTotalHeight() {
        return totalHeight;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getBeepers() {

        return beeperProperty.get();
    }

    public ArrayList<Outer> getCellWalls() {
        return cellWalls;
    }

    public BeeperGUI getBeeperGUIManager() {
        return beeperGUIManager;
    }

    public void setBeepers(int beepers) {
        this.beeperProperty.set(beepers);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Outer || o instanceof Rectangle || o instanceof Cell))
            return false;
        return getChildren().contains(o);
    }

    @Override
    public String toString() {

        return String.format("[%d][%b],", beeperProperty.get(), checkWalls());
    }

    private boolean checkWalls() {
        for (Node n : getChildren())
            if (n instanceof Outer) {
                Outer outer = (Outer) n;
                if (outer.isWall())
                    return true;
            }
        return false;
    }
}
