package com.chatapp.client.components.GroupMemberDialog;

import com.chatapp.client.components.FriendBox.FriendBox;
import com.chatapp.client.components.FriendBox.FriendBoxType;
import com.chatapp.client.components.FriendBox.PendingFriendBox;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ConversationRequest;
import com.chatapp.commons.request.SearchRequest;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class GroupMemberDialog extends DialogPane {
    @FXML
    protected VBox addedPanel;
    @FXML
    protected TextField searchFriend;
    @FXML
    protected BorderPane notFound;
    @FXML
    protected VBox searchResult;
    @FXML
    protected StackPane stackContent;
    protected Button cancelBtn;

    @Setter
    private int userId;
    @Setter
    @Getter
    private Conversation conversation;

    private Dialog dialog = new Dialog();

    private UserSocketService userSocketService = UserSocketService.getInstance();

    public GroupMemberDialog() {
        FXMLLoader loader = new FXMLLoader(GroupMemberDialog.class.getResource("GroupMemberDialog.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            dialog.setDialogPane(this);
            stackContent.visibleProperty().bind(searchFriend.textProperty().isNotEmpty());

            initGui();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void loadData() {
        addedPanel.getChildren().clear();
        Properties body = new Properties();
        body.put("conId", conversation.getId());
        body.put("class", GroupMemberDialog.class);
        userSocketService.addRequest(
                ConversationRequest.builder()
                        .action(Action.GET_MEMBERS)
                        .body(body)
                        .build()
        );
    }

    public void initGui() {
        cancelBtn = (Button) this.lookupButton(ButtonType.CANCEL);

        cancelBtn.setStyle("-fx-text-fill: black; -fx-font-weight: 800; -fx-cursor: hand");
        cancelBtn.setText("HUỶ");

        cancelBtn.setOnAction(e -> this.dialog.close());
        searchFriend.textProperty().addListener(onSearchFriend());
    }

    private ChangeListener<String> onSearchFriend() {
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue,
                                String oldValue,
                                String newValue) {
                if (newValue.isBlank()) return;

                pauseTransition.setOnFinished((e) -> {
                    Properties params = new Properties();
                    params.put("keyword", newValue.trim());
                    System.out.println(params);

                    userSocketService.addRequest(
                            SearchRequest.builder()
                            .action(Action.SEARCH_FRIEND)
                            ._class(GroupMemberDialog.class)
                            .params(params)
                            .build());
                });
                pauseTransition.playFromStart();
            }
        };
    }

    public EventHandler onUserBoxClick(FriendBox ins) {
        return new EventHandler() {
            @Override
            public void handle(Event event) {
                // Gui req them thanh vien toi server
                searchResult.getChildren().remove(ins);
                if (!addedPanel.getChildren().stream().anyMatch(
                        (value) -> {
                            return ((FriendBox) value).getUserId() == ins.getUserId();
                        }
                )) {
                    addedPanel.getChildren().add(ins);
                    Properties body = new Properties();
                    body.put("conId", conversation.getId());
                    body.put("userId", ins.getUserId());
                    userSocketService.addRequest(
                            ConversationRequest.builder()
                                    .action(Action.ADD_MEMBER)
                                    .body(body)
                                    .build()
                    );
                    loadData();
                } else {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Cảnh báo");
                    warning.setHeaderText(null);
                    warning.setContentText("Đã có trong danh sách thành viên");
                    warning.showAndWait();
                }
            }
        };
    }

    public void loadMemberList(Object result) {
        Platform.runLater(() -> {
            Properties body = (Properties) result;
            List<User> users = (List<User>) body.get("members");
            List<User> admins = (List<User>) body.get("admins");
            System.out.println(users);
            System.out.println(admins);
            admins.forEach(user -> {
                FriendBox ins = null;
                if (user.getId() == userId) {
                    ins = FriendBox.toFriendBox(user);
                    ins.setOnMouseClicked(null);
                }
                else {
                    PendingFriendBox penIns = PendingFriendBox.toPendingFriendBox(user, FriendBoxType.UNFRIEND);
                    penIns.setCancelBtnTooltip("Kích khỏi nhóm");
                    penIns.setCancelBtnAction(e -> {
                        Properties body1 = new Properties();
                        body1.put("conId", conversation.getId());
                        body1.put("userId", penIns.getUserId());
                        userSocketService.addRequest(
                                ConversationRequest.builder()
                                        .action(Action.KICK_MEMBER)
                                        .body(body1)
                                        .build()
                        );
                        this.loadData();
                    });
                    ins = penIns;
                }
                ins.setStyle("-fx-background-color: #77a5cb");
                Tooltip.install(ins, new Tooltip("Admin của nhóm"));
                addedPanel.getChildren().add(ins);
            });
            if (users != null)
                users.forEach(user -> {
                    if (user == null) return;
                    FriendBox ins = null;
                    ins = PendingFriendBox.toPendingFriendBox(user);
                    PendingFriendBox penIns = (PendingFriendBox) ins;
                    penIns.setCancelBtnTooltip("Kick khỏi nhóm");
                    penIns.setAcceptBtnTooltip("Chuyển quyền admin");
                    penIns.setAcceptBtnAction(e -> {
                        Properties body1 = new Properties();
                        body1.put("conId", conversation.getId());
                        body1.put("userId", penIns.getUserId());
                        userSocketService.addRequest(
                                ConversationRequest.builder()
                                        .action(Action.CHANGE_ADMIN)
                                        .body(body1)
                                        .build()
                        );
                        this.loadData();
                    });
                    penIns.setCancelBtnAction(e -> {
                        Properties body1 = new Properties();
                        body1.put("conId", conversation.getId());
                        body1.put("userId", penIns.getUserId());
                        userSocketService.addRequest(
                                ConversationRequest.builder()
                                        .action(Action.KICK_MEMBER)
                                        .body(body1)
                                        .build()
                        );
                        this.loadData();
                    });
                    addedPanel.getChildren().add(ins);
                });
        });
    }

    public void loadSearchResult(Object result) {
        Platform.runLater(() -> {
            searchResult.getChildren().clear();
            if (result == null) {
                notFound.toFront();
                notFound.setVisible(true);
                return;
            } else {
                notFound.toBack();
                notFound.setVisible(false);
                searchResult.toFront();
            }

            if (result instanceof User) {
                FriendBox ins = FriendBox.toFriendBox((User) result);
                Tooltip.install(ins, new Tooltip(((User) result).getUsername()));
                ins.setOnMouseClicked(onUserBoxClick(ins));
                searchResult.getChildren().add(ins);
            }
        });
    }

    public void show() {
        loadData();
        dialog.show();
    }
}
