package com.chatapp.client.components.FriendBox;

import com.chatapp.client.components.Avatar.Avatar;
import com.chatapp.client.components.ConversationBox.ConversationBox;
import com.chatapp.commons.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import java.sql.Blob;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class FriendBox extends HBox implements Initializable {
    @FXML
    private BorderPane avatarContainer;
    @FXML
    private Label name;

    private Avatar avatar;

    @Setter
    @Getter
    private int userId;

    protected void setData(User user) {
        String name = (user.getFullName() == null || user.getFullName().isEmpty()) ? user.getUsername() : user.getFullName();
        this.setName(name);
        this.setUserId(user.getId());
        this.setActive(user.getIsActive());
        try {
//            Blob dbAvatar = user.getAvatar();
//            if (dbAvatar != null && dbAvatar.length() == 0) {
//                Image avatar = new Image(dbAvatar.getBinaryStream());
//                this.setAvatar(avatar);
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static FriendBox toFriendBox(User user) {
        // Convert a user model to FriendBox
        FriendBox ins = new FriendBox();
        ins.setData(user);
        return ins;
    }

    public FriendBox() {
        FXMLLoader loader = new FXMLLoader(FriendBox.class.getResource("FriendBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();

            this.getStyleClass().add("box");
            avatar = new Avatar(36, 36);
            avatarContainer.setCenter(avatar);
        }catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public FriendBox(String name) {
        this();
        setName(name);
    }

    public FriendBox(Image avatar, String name) {
        this();
        setAvatar(avatar);
        setName(name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setAvatar(String imagePath) {
        this.avatar.setImage(imagePath);
    }

    public void setAvatar(Image avatar) {
        this.avatar.setImage(avatar);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setActive(boolean isActive) {
        this.avatar.setActiveStatus(isActive);
    }

    public String getNameText() {
        return this.name.getText();
    }

    public Image getAvatarImage() {
        return this.avatar.getImage();
    }

    public boolean getActive() {
        return this.avatar.getActiveStatus();
    }
}