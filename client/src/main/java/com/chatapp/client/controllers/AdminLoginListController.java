package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.request.ManageUsersRequest;
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AdminLoginListController implements Initializable {
    private final ObservableList<LoginHistoryClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView LoginList = new TableView();
    @FXML
    void turnBackAdminView(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AdminView.fxml"));
        try {
            scenePane.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

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
                                .action(Action.GET_LOGIN_LIST)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            LoginListResponse res = (LoginListResponse) waitResponse.getValue();
            List<LoginHistory> loginList =  res.getLoginList();
            if (loginList != null) {
                for (LoginHistory loginHistory : loginList) {
                    //String createdAt = Date2String(loginHistory.getCreatedAt());
                    data.add(new AdminLoginListController.LoginHistoryClone(
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
    public void initialize(URL url, ResourceBundle resourceBundle){
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

        LoginList.setItems(data);
        LoginList.getColumns().addAll(userIDColumn, userNameColumn, nameColumn, loginAtColumn);
        LoginList.setEditable(false);
    }



    public static class LoginHistoryClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty username;
        public SimpleStringProperty name;
        public SimpleObjectProperty<Timestamp> createdAt;

        private Date String2Date(String date) throws Exception{
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }

        public LoginHistoryClone(int ID, String username, String name,  Timestamp createdAt) {
            this.id = new SimpleIntegerProperty(ID);
            this.username = new SimpleStringProperty(username);
            this.name = new SimpleStringProperty(name);
            this.createdAt = new SimpleObjectProperty<>();
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
