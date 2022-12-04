package com.chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.BootstrapFX;

public class Main extends Application {

    private void configStage(Stage stage) {
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
    }

    @Override
    public void start(Stage stage) throws Exception {
        configStage(stage);

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/UserView.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(String.valueOf(Main.class.getResource("css/GlobalStyle.css")));
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}