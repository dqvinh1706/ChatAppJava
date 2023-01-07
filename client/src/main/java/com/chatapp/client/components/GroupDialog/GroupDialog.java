package com.chatapp.client.components.GroupDialog;

import com.chatapp.client.components.FriendBox.FriendBox;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ConversationRequest;
import com.chatapp.commons.request.SearchRequest;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class GroupDialog extends DialogPane {
    @FXML
    private VBox addedPanel;
    @FXML
    private TitledPane titledPane;
    @FXML
    private TextField searchFriend;
    @FXML
    private BorderPane notFound;
    @FXML
    private VBox searchResult;
    @FXML
    private StackPane stackContent;
    private Button createGrBtn, cancelBtn;
    private Dialog dialog = new Dialog();

    private UserSocketService userSocketService = UserSocketService.getInstance();

    public GroupDialog() {
        FXMLLoader loader = new FXMLLoader(GroupDialog.class.getResource("GroupDialog.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            dialog.setDialogPane(this);

            stackContent.visibleProperty().bind(searchFriend.textProperty().isNotEmpty());

            createGrBtn = (Button) this.lookupButton(ButtonType.OK);
            cancelBtn = (Button) this.lookupButton(ButtonType.CANCEL);

            createGrBtn.setStyle("-fx-text-fill: #0080ff; -fx-font-weight: 800; -fx-cursor: hand");
            createGrBtn.setText("TẠO NHÓM");
            createGrBtn.setDisable(true);
            cancelBtn.setStyle("-fx-text-fill: black; -fx-font-weight: 800; -fx-cursor: hand");
            cancelBtn.setText("HUỶ");

            addedPanel.getChildren().addListener(onChangeAddedPanel());

            createGrBtn.setOnAction(onCreateGrClicked());
            cancelBtn.setOnAction(e -> this.dialog.close());
            searchFriend.textProperty().addListener(onSearchFriend());
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private ListChangeListener onChangeAddedPanel() {
        return new ListChangeListener<>() {
            @Override
            public void onChanged(Change<?> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        FriendBox ins = (FriendBox) change.getAddedSubList().get(0);
                        Tooltip.install(ins, new Tooltip("Xoá khỏi nhóm"));
                        ins.setOnMouseClicked(e -> {
                            addedPanel.getChildren().remove(ins);
                        });
                        if (change.getList().size() < 2) {
                            return;
                        }
                        createGrBtn.setDisable(false);
                    }
                }
            }
        };
    }

    public EventHandler onCreateGrClicked() {
        return new EventHandler() {
            @Override
            public void handle(Event event) {
                List<Integer> membersId = addedPanel.getChildren().stream().map((Node node) -> {
                    FriendBox ins = (FriendBox) node;
                    return ins.getUserId();
                }).collect(Collectors.toList());
                userSocketService.addRequest(ConversationRequest.builder()
                        .action(Action.CREATE_GROUP_CHAT)
                        .body(membersId)
                        .build());
            }
        };
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
                    userSocketService.addRequest(SearchRequest.builder()
                            .action(Action.SEARCH_FRIEND)
                            .params(params)
                            .build());
                });
                pauseTransition.playFromStart();
            }
        };
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
                ins.setOnMouseClicked(e -> {
                    searchResult.getChildren().remove(ins);
                    if (!addedPanel.getChildren().stream().anyMatch(
                            (value) -> {
                                return ((FriendBox) value).getUserId() == ins.getUserId();
                            }
                    )) {
                        addedPanel.getChildren().add(ins);
                    } else {
                        Alert warning = new Alert(Alert.AlertType.WARNING);
                        warning.setTitle("Cảnh báo");
                        warning.setHeaderText(null);
                        warning.setContentText("Đã thêm bạn bè vô danh sách nhóm");
                        warning.showAndWait();
                    }
                });
                searchResult.getChildren().add(ins);
            }
        });
    }

    public void show() {
        dialog.show();
    }
}
