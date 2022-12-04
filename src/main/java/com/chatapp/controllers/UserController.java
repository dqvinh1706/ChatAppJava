package com.chatapp.controllers;

import com.chatapp.StoreContext;
import com.chatapp.components.Avatar.Avatar;
import com.chatapp.components.ConversationBox.ConversationBox;
import com.chatapp.models.Conversation;
import com.chatapp.models.User;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;


public class UserController implements Initializable {
    private SimpleObjectProperty<User> currUser = StoreContext.getInstance().getMainUser();
    // Sidebar
    @FXML
    private VBox sidebarContainer;
    @FXML
    private Button messageTabBtn, friendsTabBtn, accountBtn;
    @FXML
    private ContextMenu popupMenu;

    // Tab
    @FXML
    private VBox tabHeader;

    @FXML
    private Label tabTitle;

    @FXML
    private Button addFriend;

    @FXML
    private Button createGroup;

    @FXML
    private HBox searchContainer;

    @FXML
    private ScrollPane tabContent;
    @FXML
    private VBox content;

    @FXML
    private HBox chatHeader;

    @FXML
    private HBox chatInfoContainer;

    @FXML
    private Label chatTitle;

    @FXML
    private VBox chatContent;

    @FXML
    private TextField messageText;

    private Avatar currInboxAvatar;

    private Conversation conversation;

    public UserController() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configSidebar();
        configTabContainer();
        configChatHeader();
        loadConversationTab();
        currUser.addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observableValue, User user, User t1) {
                if (t1 != null) {
                    System.out.println(t1.toString());
                }
            }
        });
    }

    public void setUserData(User user) {
        this.currUser.set(user);
    }

    private void configSidebar() {
        sidebarContainer.getChildren().get(0).getStyleClass().add("is-active");
        Avatar userAvatar = new Avatar(36, 36);
        userAvatar.disabledActiveSymbol();
        accountBtn.setGraphic(userAvatar);
    }

    private void configTabContainer() {
        tabContent.getContent().setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                double deltaY = scrollEvent.getDeltaY() * 2;
                double height = tabContent.getContent().getBoundsInLocal().getHeight();
                tabContent.setVvalue(tabContent.getVvalue() - deltaY / height);
            }
        });
        tabContent.toFront();
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


    @FXML
    public void sendMessage(ActionEvent e) {
        messageText.clear();
        messageText.requestFocus();
    }

    private Node activeStyleToggle(Event e) {
        if (e.getSource() instanceof Node) {
            Node ins = (Node) e.getSource();
            Node oldActive = (Node) ins.getParent().lookup(".is-active");
            if (oldActive != null) {
                if(oldActive.equals(ins)){
                    return ins;
                }
                oldActive.getStyleClass().remove("is-active");
            }

            ins.getStyleClass().add("is-active");
            return ins;
        }

        return null;
    }

    @FXML
    public void loadTab(ActionEvent e) {
        Button ins = (Button) activeStyleToggle(e);
        content.getChildren().clear();
        switch (ins.getId()){
            case "messageTabBtn":
                loadConversationTab();
                break;
            case "friendsTabBtn":
                loadFriendsTab();
                break;
            case "editProfileTabBtn":
                loadEditProfile();
                break;
            default:
                throw new RuntimeException("Not exist button");
        }
    }

    private void loadEditProfile() {

    }

    private void loadConversationTab() {
        tabTitle.setText("Chat");

        for (int i = 0; i < 10; i++) {
            ConversationBox demo = new ConversationBox();
            Avatar demoAvatar = new Avatar(48, 48);
            demo.setAvatar(demoAvatar);

            demo.setOnMouseClicked(e -> loadChatDetail(e));
            content.getChildren().add(demo);
        }
    }

    private void loadFriendsTab() {
        tabTitle.setText("Mọi người");

        for (int i = 0; i < 2; i++) {
            ConversationBox demo = new ConversationBox();
            Avatar demoAvatar = new Avatar(48, 48);
            demo.setAvatar(demoAvatar);

            demo.setOnMouseClicked(e -> loadChatDetail(e));
            content.getChildren().add(demo);
        }
    }

    private void loadChatDetail(MouseEvent e) {
        ConversationBox instance = (ConversationBox) activeStyleToggle(e);
        currInboxAvatar.setImage(instance.getAvatarImage());
//        chatTitle.setText();
        chatContent.getChildren().clear();
    }

    @FXML
    public void onLogout(ActionEvent e) {

    }


//    private HBox myMessage() {
//        HBox myMessage = new HBox();
//        myMessage.setAlignment(Pos.CENTER_RIGHT);
//
//        return myMessage;
//    }
//
//    private HBox
}
