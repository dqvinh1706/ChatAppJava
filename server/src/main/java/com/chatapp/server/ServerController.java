package com.chatapp.server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {
    private ServerSocketService server;
    private int PORT = 5050;
    @FXML
    private Label welcomeText;

    public ServerController() {

    }

    @FXML
    protected void onStartServer() {
        if (!server.isRunning())
            server.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        server = new ServerSocketService(this);
    }

    public void setWelcomeText(String text) {
        this.welcomeText.setText(text);
    }
}