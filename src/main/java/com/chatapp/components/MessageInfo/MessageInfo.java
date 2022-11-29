package com.chatapp.components.MessageInfo;

import com.chatapp.components.Avatar.Avatar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessageInfo extends HBox implements Initializable {

    @FXML
    private BorderPane avatarContainer;

    @FXML
    private Label message;

    @FXML
    private Label name;

    @FXML
    private Label time;

    private Avatar avatar;

    public MessageInfo() {
        FXMLLoader loader = new FXMLLoader(MessageInfo.class.getResource("MessageInfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
        }catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setName(String nameText){
        this.name.setText(nameText);
    }

    public void setMessage(String messageText) {
        this.message.setText(messageText);
    }

    public Label getMessageLabel() {
        return this.message;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        avatarContainer.setCenter(this.avatar);
    }

    public void setActive(boolean isActive) {
        this.avatar.setActive(isActive);
    }

    public Image getAvatarImage() {
        return this.avatar.getImage();
    }
}
