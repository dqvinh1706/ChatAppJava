package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.FriendListResponse;
import com.chatapp.commons.response.Response;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ShowFriendListController implements Initializable {
    private final ObservableList<FriendListClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    private int SelectedID = -1;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView<FriendListClone> FriendList = new TableView<>();

    private void getData(){
        if(!userSocketService.isRunning()) userSocketService.start();
        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.GET_FRIEND_BY_ID)
                                .body(SelectedID)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            FriendListResponse res = (FriendListResponse) waitResponse.getValue();
            List<User> friendList =  res.getFriendList();

            for(User user: friendList){
                data.add(new ShowFriendListController.FriendListClone(
                        user.getId(),
                        user.getUsername(),
                        user.getFullName()
                ));
            }
        });
        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
    }

    public void setValue(int SelectedID){
        this.SelectedID = SelectedID;

        this.getData();

        TableColumn<FriendListClone, Integer> userIDColumn = new TableColumn<FriendListClone, Integer>("User ID");
        TableColumn<FriendListClone, String> userNameColumn = new TableColumn<FriendListClone, String>("Username");
        TableColumn<FriendListClone, String> nameColumn = new TableColumn<FriendListClone, String>("Name");

        userIDColumn.setCellValueFactory(new PropertyValueFactory<FriendListClone, Integer>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<FriendListClone, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<FriendListClone, String>("name"));

        userIDColumn.setMinWidth(320.0);
        userNameColumn.setMinWidth(320.0);
        nameColumn.setMinWidth(320.0);

        FriendList.setItems(data);
        FriendList.getColumns().addAll(userIDColumn, userNameColumn, nameColumn);
        FriendList.setEditable(false);
    }
    public static class FriendListClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty username;
        public SimpleStringProperty name;

        public FriendListClone(int ID, String username, String name){
            this.id = new SimpleIntegerProperty(ID);
            this.username = new SimpleStringProperty(username);
            this.name = new SimpleStringProperty(name);
        }

        public int getId() {
            return id.get();
        }

        public String getUsername() {
            return username.get();
        }

        public String getName() {return name.get();}
    }
}
