package com.chatapp.client.controllers;

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

public class ShowGroupAdminListController implements Initializable {
    private final ObservableList<GroupAdminListClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    private int SelectedID = -1;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView<GroupAdminListClone> AdminList = new TableView<>();

    private void getData(){
        if(!userSocketService.isRunning()) userSocketService.start();
        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.GET_ADMIN_BY_GROUP_ID)
                                .body(SelectedID)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            FriendListResponse res = (FriendListResponse) waitResponse.getValue();
            List<User> adminList =  res.getFriendList();
            if (adminList != null) {
                for (User user : adminList) {
                    data.add(new ShowGroupAdminListController.GroupAdminListClone(
                            user.getId(),
                            user.getUsername(),
                            user.getFullName()
                    ));
                }
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

        TableColumn<GroupAdminListClone, Integer> userIDColumn = new TableColumn<GroupAdminListClone, Integer>("User ID");
        TableColumn<GroupAdminListClone, String> userNameColumn = new TableColumn<GroupAdminListClone, String>("Username");
        TableColumn<GroupAdminListClone, String> nameColumn = new TableColumn<GroupAdminListClone, String>("Name");

        userIDColumn.setCellValueFactory(new PropertyValueFactory<GroupAdminListClone, Integer>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<GroupAdminListClone, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<GroupAdminListClone, String>("name"));

        userIDColumn.setMinWidth(320.0);
        userNameColumn.setMinWidth(320.0);
        nameColumn.setMinWidth(320.0);

        AdminList.setItems(data);
        AdminList.getColumns().addAll(userIDColumn, userNameColumn, nameColumn);
        AdminList.setEditable(false);
    }
    public static class GroupAdminListClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty username;
        public SimpleStringProperty name;

        public GroupAdminListClone(int ID, String username, String name){
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
