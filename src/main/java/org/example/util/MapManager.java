package org.example.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.edit.Cell;
import org.example.items.Beeper;
import org.example.items.Wall;
import org.example.screens.Editor;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MapManager {

    private File currentFile;
    private FileChooser fileChooser;
    private ArrayList<Beeper> beepers;
    private ArrayList<Wall> walls;
    private int worldWidth;
    private int worldHeight;
    private Editor editor;

    public MapManager(Editor editor) {
        this.editor = editor;
        this.beepers = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.currentFile = null;
        this.fileChooser = new FileChooser();
        this.worldWidth = 0;
        this.worldHeight = 0;
    }

    public MapManager(File currentFile) {

        this.currentFile = currentFile;
    }


    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        editor.getAppStart().getWindow().setTitle("MapBuilder\t" + currentFile);
        this.currentFile = currentFile;
        this.fileChooser.setInitialDirectory(currentFile.getParentFile());
    }

    public void loadMap(File fileChosen) {
        setCurrentFile(fileChosen);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            try {
                doc = builder.parse(fileChosen);
            } catch (NullPointerException e) {
                System.err.println("file not initialized");

                return;
            }
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            Node world = doc.getElementsByTagName("world").item(0);

            Node properties = doc.getElementsByTagName("properties").item(0);
            NodeList propertyNodes = properties.getChildNodes();
            if (propertyNodes.getLength() > 0) {
                for (int k = 0; k < properties.getChildNodes().getLength(); k++) {
                    Node property = propertyNodes.item(k);
                    if (property.getNodeName().equals("defaultSize") && property.hasAttributes()) {
                        NamedNodeMap attributes = property.getAttributes();
                        for (int i = 0; i < attributes.getLength(); i++) {
                            Node attribute = attributes.item(i);
                            switch (attribute.getNodeName()) {

                                case "width":
                                    worldWidth = Integer.parseInt(attribute.getNodeValue());
                                    break;
                                case "height":
                                    worldHeight = Integer.parseInt(attribute.getNodeValue());
                                    break;

                            }
                        }
                    }
                }
            } else {
                worldWidth = 10;
                worldHeight = 10;
            }
            Node objects = doc.getDocumentElement().getElementsByTagName("objects").item(0);
            System.out.println("properties length: " + properties.getChildNodes().getLength());
            System.out.println(objects.getChildNodes().getLength());
            NodeList nodes = objects.getChildNodes();
            System.out.println();
            for (int i = 0; i < nodes.getLength(); i++) {

                Node node = nodes.item(i);

                if (node.hasAttributes()) {
                    NamedNodeMap attributes = node.getAttributes();
                    System.out.println("Node: " + node.getNodeName());
                    System.out.println("Attribute Length: " + attributes.getLength());
                    int x = -1, y = -1;

                    switch (node.getNodeName()) {

                        case "beeper":
                            int num = -1;
                            for (int j = 0; j < attributes.getLength(); j++) {
                                Node attribute = attributes.item(j);
                                switch (attribute.getNodeName()) {
                                    case "num":
                                        num = Integer.parseInt(attribute.getNodeValue());
                                        break;
                                    case "x":
                                        x = Integer.parseInt(attribute.getNodeValue())-1;
                                        break;
                                    case "y":
                                        y = worldHeight - Integer.parseInt(attribute.getNodeValue());
                                        break;

                                }
                            }

                            Beeper beeper = new Beeper(x, y, num);

                            beepers.add(beeper);
                            break;
                        case "wall":
                            Wall.WallStyle style = null;
                            int length = -1;
                            for (int j = 0; j < attributes.getLength(); j++) {
                                Node attribute = attributes.item(j);
                                switch (attribute.getNodeName()) {
                                    case "style":
                                        style = Wall.WallStyle.valueOf(attribute.getNodeValue().toUpperCase());
                                        break;
                                    case "x":
                                        x = Integer.parseInt(attribute.getNodeValue());
                                        System.out.println("beeper Y: " + y);
                                        break;
                                    case "y":
                                        y = worldHeight - Integer.parseInt(attribute.getNodeValue());
                                        System.out.println("beeper Y: " + y);
                                        break;
                                    case "length":
                                        length = Integer.parseInt(attribute.getNodeValue());
                                }
                            }
                            if (style == Wall.WallStyle.HORIZONTAL)
                                x--;
                            Wall wall = new Wall(x, y, length, style);
                            walls.add(wall);

                    }
                    System.out.println();
                    System.out.println();
                }
            }

            System.out.println();
            System.out.println("results: ");
            System.out.println(beepers);
            System.out.println(walls);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        editor.setEditorWidth(worldWidth);
        editor.setEditorHeight(worldHeight);
    }

    public void constructMap(Cell[][] cells) {
        //refreshing lists to be used in saveMap()
        clearLists();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                Beeper beeper = cell.getBeeperGUIManager().getBeeperBacking();
                if (beeper.getNum() != 0)
                    beepers.add(beeper);

                for (Cell.Outer wall : cell.getCellWalls()) {
                    if (!wall.isUnusable() && wall.isWall())
                        walls.add(wall.getWallBacking());
                }
            }
        }

    }

    public void saveMap(File fileChosen) {

        setCurrentFile(fileChosen);

        try {
            DocumentBuilderFactory buildFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = buildFactory.newDocumentBuilder();
            Document mapFile = builder.newDocument();
            Element world = mapFile.createElement("world");
            mapFile.appendChild(world);

            Element properties = mapFile.createElement("properties");
            world.appendChild(properties);
            Element defaultSize = mapFile.createElement("defaultSize");
            properties.appendChild(defaultSize);
            Attr defaultWidth = mapFile.createAttribute("width");
            defaultWidth.setValue(editor.getEditorWidth() + "");
            defaultSize.setAttributeNode(defaultWidth);
            Attr defaultHeight = mapFile.createAttribute("height");
            defaultHeight.setValue(editor.getEditorHeight() + "");
            defaultSize.setAttributeNode(defaultHeight);
            Element objects = mapFile.createElement("objects");
            world.appendChild(objects);
            System.out.println(beepers);
            System.out.println(walls);
            for (Beeper beeper : beepers) {
                Element element = mapFile.createElement("beeper");

                Attr x = mapFile.createAttribute("x");

                x.setValue(beeper.getX() + 1 + "");
                Attr y = mapFile.createAttribute("y");

                y.setValue(editor.getEditorHeight() - beeper.getY() + "");
                Attr num = mapFile.createAttribute("num");
                num.setValue(beeper.getNum() + "");

                element.setAttributeNode(x);
                element.setAttributeNode(y);
                element.setAttributeNode(num);

                objects.appendChild(element);
            }
            for (Wall wall : walls) {
                Element element = mapFile.createElement("wall");

                Attr style = mapFile.createAttribute("style");
                style.setValue(wall.getStyle().toString().toLowerCase());

                Attr x = mapFile.createAttribute("x");
                x.setValue(wall.getX() + "");
                if (wall.getStyle() == Wall.WallStyle.HORIZONTAL)
                    x.setValue((wall.getX()+1)+"");

                Attr y = mapFile.createAttribute("y");
                y.setValue(editor.getEditorHeight() - wall.getY() + "");

                Attr length = mapFile.createAttribute("length");
                length.setValue(wall.getLength() + "");

                element.setAttributeNode(style);
                element.setAttributeNode(x);
                element.setAttributeNode(y);
                element.setAttributeNode(length);

                objects.appendChild(element);
            }

            TransformerFactory formFactory = TransformerFactory.newInstance();
            Transformer transformer = formFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMSource source = new DOMSource(mapFile);
            StreamResult result = new StreamResult(fileChosen);

            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

    }

    public void setMapObjects(Cell[][] cells) {
        for (Beeper beeper : beepers) {
            cells[beeper.getY()][beeper.getX()].setBeepers(beeper.getNum());
        }
        for (Wall wall : walls) {
            int y = wall.getY(), x = wall.getX();

            if (wall.getStyle().equals(Wall.WallStyle.HORIZONTAL)) {
                Cell.Outer top, bot;
                Cell cell;
                if (y != cells.length) {
                     cell = cells[y][x];
                    top = (Cell.Outer) cell.getCellManager().retrieve("top");


                    if (!top.isUnusable())
                        top.setWall(true);
                    else {
                        cell = cells[y - 1][x];
                        bot = (Cell.Outer) cell.getCellManager().retrieve("bot");
                        if (!bot.isUnusable())
                            bot.setWall(true);

                    }
                } else {
                    cell = cells[y - 1][x];
                    bot = (Cell.Outer) cell.getCellManager().retrieve("bot");
                    if (!bot.isUnusable())
                        bot.setWall(true);
                }

            } else if (wall.getStyle().equals(Wall.WallStyle.VERTICAL)) {
                Cell.Outer left, right;
                Cell cell;
                if (x != cells[0].length) {
                    cell = cells[y][x];
                    left = (Cell.Outer) cell.getCellManager().retrieve("left");
                    right = (Cell.Outer) cell.getCellManager().retrieve("right");


                    if (!left.isUnusable())
                        left.setWall(true);

                    else {
                        cell = cells[y][x - 1];
                        right = (Cell.Outer) cell.getCellManager().retrieve("right");
                        if (!right.isUnusable())
                            right.setWall(true);

                    }

                } else {
                    cell = cells[y][x - 1];
                    right = (Cell.Outer) cell.getCellManager().retrieve("right");
                    if (!right.isUnusable())
                        right.setWall(true);

                }


            }
        }

    }

    public ArrayList<Beeper> getBeepers() {
        return beepers;
    }

    public void setBeepers(ArrayList<Beeper> beepers) {
        this.beepers = beepers;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public void setWalls(ArrayList<Wall> walls) {
        this.walls = walls;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public void setWorldWidth(int worldWidth) {
        this.worldWidth = worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public void clearLists() {
        beepers.clear();
        walls.clear();
    }

    public File getSaveFile() {

        fileChooser.setTitle("choose save location");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("map files", "*.MAP"));

        return fileChooser.showSaveDialog(new Stage());
    }

    public File getOpenFile() {

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("map files", "*.map"));
        fileChooser.setTitle("choose a map file to open");

        return fileChooser.showOpenDialog(new Stage());
    }
}
