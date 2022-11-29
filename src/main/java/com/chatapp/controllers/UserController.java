package com.chatapp.controllers;

import com.chatapp.components.Avatar.Avatar;
import com.chatapp.components.MessageInfo.MessageInfo;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;


public class UserController implements Initializable {
    @FXML
    private Button accountBtn;

    @FXML
    private Button friendsTabBtn;

    @FXML
    private Button addFriend;

    @FXML
    private Button createGroup;

    @FXML
    private ColumnConstraints mainColumnConstraints;

    @FXML
    private Button messageTabBtn;

    @FXML
    private VBox personalChatContainer;

    @FXML
    private VBox mainContainer;
    @FXML
    private VBox tabHeader;
    @FXML
    private StackPane tabContent;

    @FXML
    private ScrollPane conversationContainer;
    @FXML
    private VBox conversationList;
    @FXML
    private HBox searchContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private HBox chatHeader;

    @FXML
    private HBox chatInfoContainer;

    @FXML
    private Label chatTitle;

    @FXML
    private TextField messageText;

    private Avatar currInboxAvatar;

    public UserController() {
//        UserModel model = new UserModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configSidebar();
        configConversationContainer();
        configChatHeader();
        loadConversationList();
    }

    private void configSidebar() {
        messageTabBtn.getStyleClass().add("is-active");
    }

    private void configConversationContainer() {
        conversationContainer.getContent().setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                double deltaY = scrollEvent.getDeltaY() * 2;
                double height = conversationContainer.getContent().getBoundsInLocal().getHeight();
                conversationContainer.setVvalue(conversationContainer.getVvalue() - deltaY / height);
            }
        });
        conversationContainer.toFront();
    }

    public void configChatHeader() {
        currInboxAvatar = new Avatar(40, 40);
        currInboxAvatar.imageProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)) {
                System.out.println("Changed");
                currInboxAvatar.setImage(newValue);
            }
        }));
        chatInfoContainer.getChildren().add(0, currInboxAvatar);
    }

    private void loadConversationList() {
        for (int i = 0; i < 10; i++) {
            MessageInfo demo = new MessageInfo();
            Avatar demoAvatar = new Avatar(48, 48);
            demo.setAvatar(demoAvatar);

            demo.setOnMouseClicked(e -> loadChatDetail(e));
            conversationList.getChildren().add(demo);
        }
    }
    private void loadChatDetail(MouseEvent mouseEvent) {

        MessageInfo instance = (MessageInfo) mouseEvent.getSource();

        currInboxAvatar.setImage(instance.getAvatarImage());
    }

    @FXML
    public void sendMessage(ActionEvent e) {
        messageText.clear();
        messageText.requestFocus();
    }

    @FXML
    public void switchToChatTap(ActionEvent e) {

    }

    @FXML
    public void switchToFriendsTab(ActionEvent e) {

    }
}
