package com.chatapp.client.components.FriendDialog;

import com.chatapp.client.components.FriendBox.FriendBox;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.FriendRequest;
import com.chatapp.commons.request.SearchRequest;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FriendDialog extends DialogPane implements Initializable {

    @FXML
    private TextField searchFriend;
    private Button addBtn, cancelBtn;
    @FXML
    private VBox searchResult;
    @FXML
    private BorderPane notFound;
    private Dialog dialog = new Dialog();
    private UserSocketService userSocketService = UserSocketService.getInstance();
    public FriendDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(FriendDialog.class.getResource("FriendDialog.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        dialog.setDialogPane(this);
        loader.load();

        addBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        addBtn.setDisable(true);
        addBtn.setOnAction(onAddPendingFriend());

        UserSocketService userSocketService = UserSocketService.getInstance();
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        searchFriend.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isBlank()) return;

            pauseTransition.setOnFinished((e) -> {
                System.out.println(newValue);
                //Gửi Search Request

                Properties params = new Properties();
                params.put("keyword", newValue.trim());
                userSocketService.addRequest(SearchRequest.builder()
                        .action(Action.SEARCH_USER)
                        .params(params)
                        ._class(FriendDialog.class)
                        .build());
            });

            pauseTransition.playFromStart();
        });
    }
    public EventHandler onAddPendingFriend() {
        return new EventHandler() {
            @Override
            public void handle(Event event) {
                List<Integer> pendingFriendId = searchResult.getChildren().stream().map((Node node) -> {
                    FriendBox ins = (FriendBox) node;
                    return ins.getUserId();
                }).collect(Collectors.toList());
                System.out.println(pendingFriendId.get(0));
                userSocketService.addRequest(FriendRequest.builder()
                        .action(Action.ADD_PENDING_FRIEND)
                        .body(pendingFriendId.get(0))
                        .build());

            }
        };
    }
    public void show() {
        dialog.showAndWait();
    }
    public void loadSearchResult(User user) {
        Platform.runLater(() -> {
            searchResult.getChildren().clear();
            if(user == null) {
                notFound.toFront();
                notFound.setVisible(true);
                searchResult.toBack();
                addBtn.setDisable(true);
//                notFound
                return;
            }
            else{
                notFound.toBack();
                searchResult.toFront();
                notFound.setVisible(false);
                addBtn.setDisable(false);
            }
            FriendBox ins = FriendBox.toFriendBox(user);
            searchResult.getChildren().add(0, ins);

        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
