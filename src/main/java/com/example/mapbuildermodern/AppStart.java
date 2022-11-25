package com.example.mapbuildermodern;
import com.example.mapbuildermodern.screens.Editor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.util.NodeManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class AppStart extends Application {

    public final int screenWidth = 1080;
    public final int screenHeight = 650;
    private final NodeManager nodeManager = new NodeManager();

    private Stage window;
    @Override
    public void start(Stage primaryStage) {

        window = primaryStage;
        Pane root = new Pane();
        BorderPane uiPane = new BorderPane();
        uiPane.setMinSize(0,0);
        uiPane.minWidthProperty().bind(window.widthProperty());
        uiPane.minHeightProperty().bind(window.heightProperty());

        uiPane.setPadding(new Insets(10));
        root.setBackground(new Background(new BackgroundFill(Color.web("#8d959e"), null, null)));

        VBox ui = new VBox();

        ui.setSpacing(50);
        ui.setAlignment(Pos.CENTER);

        Label title = new Label("Karel Map Builder");

        title.setFont(Font.font("Cascadia Code", FontWeight.NORMAL, FontPosture.REGULAR, 75));
        title.setGraphicTextGap(20);

        ImageView icon = new ImageView(getClass().getResource("assets/Karel_Icon.png").toExternalForm());
        icon.setSmooth(true);
        icon.setViewport(new Rectangle2D(0, 0, icon.getImage().getWidth(), icon.getImage().getHeight()));
        title.setGraphic(icon);

        Rectangle uiBlock = new Rectangle();
        uiBlock.yProperty().bind(window.heightProperty().divide(2).subtract(50));
        uiBlock.widthProperty().bind(window.widthProperty());
        uiBlock.heightProperty().bind(window.heightProperty().divide(3));
        uiBlock.setFill(Color.rgb(50,50,50,0.5));

        Button toEditor = new Button("create new map");
        toEditor.setPrefSize(200, 50);
        toEditor.setOnAction(e -> {
            primaryStage.getScene().setRoot(new Editor(this, 10, 10));
        });

        Pane karelSplash = setAnimation();

        nodeManager.give("rootAppStart", uiPane);
        nodeManager.give("uiAppStart", ui);
        nodeManager.give("titleAppStart", title);
        nodeManager.give("toEditorAppStart", toEditor);
        nodeManager.give("iconAppStart", icon);

        ui.getChildren().addAll(title, toEditor);
        uiPane.getChildren().add(uiBlock);
        uiPane.setCenter(ui);
        root.getChildren().add(karelSplash);
        root.getChildren().add(uiPane);

        Scene scene = new Scene(root, screenWidth, screenHeight);
        scene.getStylesheets().add(getClass().getResource("assets/Application.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("MapBuilder");
        primaryStage.show();

        primaryStage.setOnHidden(e -> {
            System.exit(0);
        });


    }

    private Pane setAnimation() {
        Pane pane = new Pane();
        Rectangle background = new Rectangle(0,0);
        background.xProperty().bind(window.widthProperty());
        background.yProperty().bind(window.heightProperty());
        background.setFill(Color.TRANSPARENT);
        pane.getChildren().add(background);

        ImageView karelSplash = new ImageView(getClass().getResource("assets/Karel_Splash.png").toExternalForm());
        karelSplash.setViewport(new Rectangle2D(0,0,karelSplash.getImage().getWidth(), karelSplash.getImage().getHeight()));
        karelSplash.setTranslateX(-karelSplash.getViewport().getWidth()/2);
        karelSplash.setTranslateY(-karelSplash.getViewport().getHeight()/2);
        karelSplash.setRotate(90);

        int xOffSet = 100, yOffSet = 100;
        karelSplash.setY(yOffSet);
        karelSplash.setX(xOffSet);
        Image backgroundImage = new Image(getClass().getResource("assets/background.png").toExternalForm());

        Timer timer = new Timer();
        timer.schedule(new TimerTask (){

            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (karelSplash.getX() > window.getWidth()) {
                        karelSplash.setX(-karelSplash.getViewport().getHeight());
                        karelSplash.setY(karelSplash.getY() + backgroundImage.getHeight());
                    }
                    if (karelSplash.getY() > window.getHeight()) {
                        karelSplash.setX(-karelSplash.getViewport().getHeight());
                        karelSplash.setY(yOffSet);
                    }
                    karelSplash.setX(karelSplash.getX() + backgroundImage.getWidth());
                });

            }
        }, new Date(), 1000);
        pane.getChildren().add(karelSplash);

        pane.setBackground(new Background(new BackgroundImage(backgroundImage,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT)));

        return pane;
    }

    public Stage getWindow() {
        return window;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public static void main(String[] args) {
        launch(args);
    }

}