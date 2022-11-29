package com.chatapp.controllers;

import com.chatapp.Main;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.util.HashMap;
class Delta { double x, y; }

public class ScreenController {
    private HashMap<String, Pane> screenMap = new HashMap<>();
    private  Scene main;
    private Stage root;
    public static ScreenController getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final ScreenController INSTANCE = new ScreenController();
    }

    public void configStage(Stage primaryStage) {
        root = primaryStage;
        root.initStyle(StageStyle.DECORATED);
//        Image icon = new Image("")
//        root.getIcons().add()
        root.setScene(main);
    }

    private void configPane(Pane pane) throws Exception {
        if (this.root == null) {
            throw new Exception("No root stage");
        }

        final Delta delta = new Delta();
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                delta.x = root.getX() - mouseEvent.getScreenX();
                delta.y = root.getY() - mouseEvent.getScreenY();
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                root.setX(mouseEvent.getScreenX() + delta.x);
                root.setY(mouseEvent.getScreenY() + delta.y);
            }
        });
    }
    private ScreenController() {
        this.main = new Scene(new AnchorPane());
        main.getStylesheets().add(String.valueOf(Main.class.getResource("css/GlobalStyle.css")));
        main.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    }

    public Scene getScreen() {
        return this.main;
    }

    public void addScreen(String name, Pane pane) throws Exception {
        if (screenMap.get(name) != null){
            throw new Exception("Already Loaded");
        }
        configPane(pane);
        screenMap.put(name, pane);
    }

    public void removeScreen(String name) {
        screenMap.remove(name);
    }

    public void switchToScreen(String name) {
        Pane currScene = screenMap.get(name);
        if (currScene == null) {
            throw new IllegalStateException("Scene not exist");
        }
        main.setRoot(screenMap.get(name));
    }

    public void setResizable(boolean isResizable) {
        root.setResizable(isResizable);
    }
}
