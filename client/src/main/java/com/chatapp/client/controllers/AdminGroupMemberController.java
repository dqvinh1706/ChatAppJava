package com.chatapp.client.controllers;

import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.Group;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.GroupMemberResponse;
import com.chatapp.commons.response.LoginHistoryResponse;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AdminGroupMemberController implements Initializable {
    private int SelectedID;
    private final ObservableList<GroupMemberClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView GroupMember = new TableView();

    private String Date2String(Date a){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(a);
    }

    private void getData(){
        if(!userSocketService.isRunning()) userSocketService.start();
        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.SHOW_GROUP_MEMBER)
                                .body(SelectedID)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            GroupMemberResponse res = (GroupMemberResponse) waitResponse.getValue();
            List<User> GroupMembers =  res.getGroupMembers();
            for(User GroupMember: GroupMembers){
                data.add(new AdminGroupMemberController.GroupMemberClone(
                        GroupMember.getId(),
                        GroupMember.getUsername(),
                        GroupMember.getFullName()
                ));
            }
        });

        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setValue(int SelectedID){
        this.SelectedID = SelectedID;
        this.getData();

        TableColumn<GroupMemberClone, Integer> userIDColumn = new TableColumn<GroupMemberClone, Integer>("User ID");
        TableColumn<GroupMemberClone, String> userNameColumn = new TableColumn<GroupMemberClone, String>("Username");
        TableColumn<GroupMemberClone, String> nameColumn = new TableColumn<GroupMemberClone, String>("Full name");

        userIDColumn.setCellValueFactory(new PropertyValueFactory<GroupMemberClone, Integer>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<GroupMemberClone, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<GroupMemberClone, String>("name"));

        userIDColumn.setMinWidth(320.0);
        userNameColumn.setMinWidth(320.0);
        nameColumn.setMinWidth(320.0);

        GroupMember.setItems(data);
        GroupMember.getColumns().addAll(userIDColumn, userNameColumn, nameColumn);
        GroupMember.setEditable(false);
    }

    public static class GroupMemberClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty username;
        public SimpleStringProperty name;

        private Date String2Date(String date) throws Exception{
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }

        public GroupMemberClone(int ID, String username, String name){
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
        public String getName() {return  name.get(); }
    }
}
