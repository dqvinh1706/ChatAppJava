package com.chatapp.client;

import com.chatapp.client.workers.AuthSocketService;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.request.AuthRequest;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Properties;

public class Main extends Application {

    private void configStage(Stage stage) {
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
    }
    @Override
    public void start(Stage stage) throws Exception {
        configStage(stage);
        try{
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(String.valueOf(Main.class.getResource("css/GlobalStyle.css")));
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

            stage.setScene(scene);
            stage.show();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        launch();
    }
}