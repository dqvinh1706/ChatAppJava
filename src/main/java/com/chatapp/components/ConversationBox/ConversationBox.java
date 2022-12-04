package com.chatapp.components.ConversationBox;

import com.chatapp.components.Avatar.Avatar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConversationBox extends HBox implements Initializable {
    @FXML
    private BorderPane avatarContainer;
    @FXML
    private Label content;
    @FXML
    private Label name;
    @FXML
    private Label time;
    private Avatar avatar;

    public ConversationBox() {
        FXMLLoader loader = new FXMLLoader(ConversationBox.class.getResource("ConversationBox.fxml"));
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

    public void setTitle(String nameText){
        this.name.setText(nameText);
    }

    public void setDescription(String messageText) {
        this.content.setText(messageText);
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        avatarContainer.setCenter(this.avatar);
    }

    public void setActiveStatus(boolean isActive) {
        this.avatar.setActiveStatus(isActive);
    }

    public Label getMessageLabel() {
        return this.content;
    }

    public Image getAvatarImage() {
        return this.avatar.getImage();
    }

    public boolean removeTimeLabel() {
        return ((Pane)time.getParent()).getChildren().remove(time);
    }

    public boolean removeMessageLabel() {
        return ((Pane) content.getParent()).getChildren().remove(content);
    }
}
