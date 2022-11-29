package com.chatapp;

import com.chatapp.controllers.ScreenController;
import com.chatapp.models.DbContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;


import java.io.IOException;

public class Main extends Application {

    private void loadAllScreen(Stage stage) throws Exception {
        ScreenController screenController = ScreenController.getInstance();
        screenController.configStage(stage);
        screenController.addScreen("login", FXMLLoader.load(Main.class.getResource("views/LoginView.fxml")));
        screenController.addScreen("signup", FXMLLoader.load(Main.class.getResource("views/SignupView.fxml")));
        screenController.addScreen("forgotPassword", FXMLLoader.load(Main.class.getResource("views/ForgotPasswordView.fxml")));
        screenController.addScreen("userView", FXMLLoader.load(Main.class.getResource("views/UserView.fxml")));
    }

    @Override
    public void start(Stage stage) throws IOException, Exception {
        ScreenController screenController = ScreenController.getInstance();
        loadAllScreen(stage);
        screenController.setResizable(false);
        screenController.switchToScreen("login");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}