package com.chatapp.client.components.FriendDialog;

import com.chatapp.client.Main;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FriendDialog extends DialogPane implements Initializable {

    private Button addBtn, cancelBtn;
    private Dialog dialog = new Dialog();
    public FriendDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(FriendDialog.class.getResource("FriendDialog.fxml"));
        System.out.println("loader");
        System.out.println(loader);
        loader.setRoot(this);
        System.out.println("set root");
        loader.setController(this);
        System.out.println("set control");
        loader.load();
        dialog.setDialogPane(this);
        System.out.println("set pane");
    }
    public void show() {
        dialog.showAndWait();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
