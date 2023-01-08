package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.SocketClient;
import com.chatapp.client.components.Avatar.Avatar;
import com.chatapp.client.components.FriendDialog.FriendDialog;
import com.chatapp.client.components.UserTabs.UserTabs;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;

import com.chatapp.commons.request.InformationRequest;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    private final SimpleObjectProperty<User> currUser = new SimpleObjectProperty<>();

    // Sidebar
    @FXML
    private Button accountBtn;

    @FXML
    private StackPane mainContainer;
    private final UserTabs userTabs = new UserTabs();

    public UserController() {
    }

    UserSocketService userSocketService = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userSocketService = UserSocketService.getInstance();
        userSocketService.restart();
        mainContainer.getChildren().add(userTabs);

        currUser.set(userSocketService.getLoggedUser());

        userTabs.toFront();
        userTabs.loadData();
        userTabs.initTab();

        configSidebar();
        currUser.addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observableValue, User user, User t1) {
                if (t1 != null) {
                }
            }
        });
    }

    private void configSidebar() {
        Avatar userAvatar = new Avatar(36, 36);
        userAvatar.disabledActiveSymbol();
        accountBtn.setGraphic(userAvatar);
    }

    private void activeStyleToggle(Node ins) {
        Node oldActive = (Node) ins.getParent().lookup(".is-active");
        if (oldActive != null) {
            if (oldActive.equals(ins)) {
                return;
            }
            oldActive.getStyleClass().remove("is-active");
        }
        ins.getStyleClass().add("is-active");
    }

    @FXML
    void onChatTabAction(ActionEvent event) {
        activeStyleToggle((Node) event.getSource());
        userTabs.toFront();
        userTabs.loadConversationTab();
    }

    @FXML
    void onFriendTabAction(ActionEvent event) {
        activeStyleToggle((Node) event.getSource());
        userTabs.toFront();
        userTabs.loadFriendTab();
    }

    @FXML
    void onPendingFriendsList(ActionEvent event) {
        activeStyleToggle((Node) event.getSource());
        userTabs.toFront();
        userTabs.loadPendingFriendsTab();
    }

    @FXML
    void onProfileTabAction(ActionEvent event) {
        activeStyleToggle((Node) event.getSource());
    }

    @FXML
    public void onLogout(ActionEvent e) {
        userSocketService.addRequest(InformationRequest.builder().action(Action.DISCONNECT).build());
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/LoginView.fxml"));
            try {
                accountBtn.getScene().setRoot(loader.load());
            } catch (IOException err) {
                throw new RuntimeException(err);
            }
        userSocketService.cancel();
    }
}
