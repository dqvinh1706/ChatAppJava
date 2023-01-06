package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.SocketClient;
import com.chatapp.client.workers.AuthSocketService;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.request.AuthRequest;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.AllUsersResponse;
import com.chatapp.commons.response.AuthResponse;
import com.chatapp.commons.response.Response;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

public class AdminUserManagerController implements Initializable {
    private ObservableList<UserClone> data;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView usersTable = new TableView();

    private void getData(){
        SocketClient socketClient = SocketClient.getInstance();
        UserSocketService userSocketService = UserSocketService.getInstance(socketClient);

        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.GET_ALL_USERS)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e->{
            AllUsersResponse res = (AllUsersResponse) waitResponse.getValue();
            System.out.println(res);
        });

        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        this.getData();
        TableColumn<UserClone, String> userNameColumn = new TableColumn<UserClone, String>("User Name");
        TableColumn<UserClone, String> nameColumn = new TableColumn<UserClone, String>("Name");
        TableColumn<UserClone, String> addressColumn = new TableColumn<UserClone, String>("Address");
        TableColumn<UserClone, String> DOBColumn = new TableColumn<UserClone, String>("Birthday");
        TableColumn<UserClone, Boolean> genderColumn = new TableColumn<UserClone, Boolean>("User Name");
        TableColumn<UserClone, String> emailColumn = new TableColumn<UserClone, String>("Email");

        userNameColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("address"));
        DOBColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("birthday"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<UserClone, Boolean>("gender"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("email"));

        userNameColumn.setMinWidth(160.0);
        nameColumn.setMinWidth(160.0);
        addressColumn.setMinWidth(160.0);
        DOBColumn.setMinWidth(160.0);
        genderColumn.setMinWidth(160.0);
        emailColumn.setMinWidth(160.0);

        usersTable.setItems(data);
        usersTable.getColumns().addAll(userNameColumn, nameColumn, addressColumn, DOBColumn, genderColumn, emailColumn);

        usersTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton()==MouseButton.SECONDARY) {
                    while(scenePane.getChildren().size() > 1) {
                        scenePane.getChildren().remove(scenePane.getChildren().size() - 1);
                    }
                    ListView<String> options = new ListView<>();
                    options.getItems().addAll("Add", "Delete", "Lock", "Update password", "Show login history", "Show friend list");

                    options.setMaxHeight(100);

                    options.setLayoutX(mouseEvent.getSceneX());
                    options.setLayoutY(mouseEvent.getSceneY());
                    scenePane.getChildren().add(options);
                }
                else if (mouseEvent.getButton() == MouseButton.PRIMARY){
                    while(scenePane.getChildren().size() > 1) {
                        scenePane.getChildren().remove(scenePane.getChildren().size() - 1);
                    }
                }
            }
        });
    }

    public static class UserClone{
        public SimpleStringProperty username;
        public SimpleStringProperty name;
        public SimpleStringProperty birthday;
        public SimpleStringProperty address;
        public SimpleBooleanProperty gender;
        public SimpleStringProperty email;

        private Date String2Date(String date) throws Exception{
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }

        public UserClone(String username, String Name, String address, String birthday, Boolean gender, String email){
            this.username = new SimpleStringProperty(username);
            this.name = new SimpleStringProperty(Name);
            this.address = new SimpleStringProperty(address);
            this.birthday = new SimpleStringProperty(birthday);
            this.gender = new SimpleBooleanProperty(gender);
            this.email = new SimpleStringProperty(email);
        }

        public String getUsername() {
            return this.username.get();
        }

        public String getName() {
            return name.get();
        }

        public String getAddress() {
            return address.get();
        }

        public String getBirthday() {
            return birthday.get();
        }

        public Boolean getGender() {
            return gender.get();
        }

        public String getEmail() {
            return this.email.get();
        }
    }
}




