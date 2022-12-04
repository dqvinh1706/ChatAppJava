package com.chatapp.controllers;

import com.chatapp.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private Button logoutButton;

    @FXML
    private AnchorPane scenePane;

    @FXML
    void goToChatList(ActionEvent event) {

    }

    @FXML
    void goToLoginList(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AdminLoginListView.fxml"));

        try {
            scenePane.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @FXML
    void goToUserManager(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AdminUserManagerView.fxml"));

        try {
            scenePane.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
    @FXML
    void logout(ActionEvent event) {
        Stage stage = (Stage) scenePane.getScene().getWindow();
        System.out.println("You successfully logged out.");
        stage.close();
    }
}
