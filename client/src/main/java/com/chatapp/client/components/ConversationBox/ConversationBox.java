package com.chatapp.client.components.ConversationBox;

import com.chatapp.client.Main;
import com.chatapp.client.components.Avatar.Avatar;
import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.utils.TimestampUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConversationBox extends HBox implements Initializable {
    @FXML
    private BorderPane avatarContainer;
    @FXML
    private Label message;
    @FXML
    private Label title;
    @FXML
    private Label time;

    private Avatar avatar;

//    @Setter
//    @Getter
//    private Integer conversationId;

    @Setter @Getter
    private Conversation conversation;

    public static ConversationBox toConversationBox(Conversation conversation) {
        ConversationBox conversationBox = new ConversationBox(conversation.getTitle());
        conversationBox.setId(conversationBox.getId() + "#" + conversation.getId());
//        conversationBox.setConversationId(conversation.getId());
        conversationBox.setConversation(conversation);
        conversationBox.setTime(TimestampUtil.convertToString(conversation.getUpdatedAt()));
        return conversationBox;
    }

    public ConversationBox() {
        FXMLLoader loader = new FXMLLoader(ConversationBox.class.getResource("ConversationBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
            this.setMaxHeight(68);
            this.setId("ConversationBox");
            this.getStyleClass().add("box");
            avatar = new Avatar(48, 48);
            avatarContainer.setCenter(avatar);
            setAvatar(Main.class.getResource("imgs/default-gr-avatar.png").toString());
        }catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public ConversationBox(String title) {
        this();
        setTitle(title);
    }

    public ConversationBox(String imagePath, String title) {
        this();
        setAvatar(imagePath);
        setTitle(title);
    }

    public ConversationBox(Image image, String title) {
        this();
        setAvatar(image);
        setTitle(title);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public void setAvatar(Image image) {
        this.avatar.setImage(image);
    }

    public void setAvatar(String imagePath) {
        this.avatar.setImage(imagePath);
    }

    public void setTitle(String nameText){
        this.title.setTooltip(new Tooltip(nameText));
        this.title.setText(nameText);
    }

    public void setMessage(String messageText) {
        this.message.setTooltip(new Tooltip(messageText));
        this.message.setText(messageText);
    }

    public void setTime(String time) {
        this.time.setTooltip(new Tooltip(time));
        this.time.setText(time);
    }

    public void setActiveStatus(boolean isActive) {
        this.avatar.setActiveStatus(isActive);
    }

    public Image getAvatarImage() {
        return this.avatar.getImage();
    }
    public String getTitle() { return this.title.getText(); }

    public Label getMessageLabel() {
        return this.message;
    }
}
