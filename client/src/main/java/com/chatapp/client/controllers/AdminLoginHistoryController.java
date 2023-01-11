package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.LoginHistoryResponse;
import com.chatapp.commons.response.LoginListResponse;
import com.chatapp.commons.response.Response;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AdminLoginHistoryController implements Initializable {
    private int SelectedID;
    private final ObservableList<LoginHistoryClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView LoginHistory = new TableView();

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
                                .action(Action.SHOW_LOGIN_HISTORY)
                                .body(SelectedID)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            LoginHistoryResponse res = (LoginHistoryResponse) waitResponse.getValue();
            List<LoginHistory> loginHistories =  res.getLoginHistories();
            if (loginHistories != null) {
                for (LoginHistory loginHistory : loginHistories) {
                    data.add(new AdminLoginHistoryController.LoginHistoryClone(
                            loginHistory.getId(),
                            loginHistory.getUsername(),
                            loginHistory.getName(),
                            loginHistory.getCreatedAt()
                    ));
                }
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

        TableColumn<LoginHistoryClone, Integer> userIDColumn = new TableColumn<LoginHistoryClone, Integer>("User ID");
        TableColumn<LoginHistoryClone, String> userNameColumn = new TableColumn<LoginHistoryClone, String>("Username");
        TableColumn<LoginHistoryClone, String> nameColumn = new TableColumn<LoginHistoryClone, String>("Full name");
        TableColumn<LoginHistoryClone, Timestamp> loginAtColumn = new TableColumn<LoginHistoryClone, Timestamp>("Login at");

        userIDColumn.setCellValueFactory(new PropertyValueFactory<LoginHistoryClone, Integer>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<LoginHistoryClone, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<LoginHistoryClone, String>("name"));
        loginAtColumn.setCellValueFactory(new PropertyValueFactory<LoginHistoryClone, Timestamp>("createdAt"));

        userIDColumn.setMinWidth(240.0);
        userNameColumn.setMinWidth(240.0);
        nameColumn.setMinWidth(240.0);
        loginAtColumn.setMinWidth(240.0);

        LoginHistory.setItems(data);
        LoginHistory.getColumns().addAll(userIDColumn, userNameColumn, nameColumn, loginAtColumn);
        LoginHistory.setEditable(false);
    }

    public static class LoginHistoryClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty username;
        public SimpleStringProperty name;
        public SimpleObjectProperty<Timestamp> createdAt;

        private Date String2Date(String date) throws Exception{
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }

        public LoginHistoryClone(int ID, String username, String name, Timestamp createdAt){
            this.id = new SimpleIntegerProperty(ID);
            this.username = new SimpleStringProperty(username);
            this.name = new SimpleStringProperty(name);

            this.createdAt = new SimpleObjectProperty<Timestamp>();
            this.createdAt.set(createdAt);
        }

        public int getId() {
            return id.get();
        }

        public String getUsername() {
            return username.get();
        }
        public String getName() {return  name.get(); }

        public Timestamp getCreatedAt() {
            return createdAt.get();
        }
    }
}
