package com.chatapp.client.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;


class UserClone{
    public String username;
    public String name;
    public Date birthday;
    public String address;
    public Boolean gender;
    public String email;

    public UserClone(String username, String Name, String address, Date birthday, Boolean gender, String email){
        this.username = username;
        this.name = Name;
        this.address = address;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Boolean getGender() {
        return gender;
    }

    public String getEmail() {
        return this.email;
    }
}

public class AdminUserManagerController implements Initializable {

    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView<UserClone> usersTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){


        TableColumn<UserClone, String> userNameColumn = new TableColumn<UserClone, String>("User Name");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("username"));

        TableColumn<UserClone, String> nameColumn = new TableColumn<UserClone, String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("fullName"));

        TableColumn<UserClone, String> addressColumn = new TableColumn<UserClone, String>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("address"));

        TableColumn<UserClone, Date> DOBColumn = new TableColumn<UserClone, Date>("Birthday");
        DOBColumn.setCellValueFactory(new PropertyValueFactory<UserClone, Date>("birthday"));

        TableColumn<UserClone, Boolean> genderColumn = new TableColumn<UserClone, Boolean>("User Name");
        genderColumn.setCellValueFactory(new PropertyValueFactory<UserClone, Boolean>("gender"));

        TableColumn<UserClone, String> emailColumn = new TableColumn<UserClone, String>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("email"));

        usersTable.getColumns().add(userNameColumn);
        usersTable.getColumns().add(nameColumn);
        usersTable.getColumns().add(addressColumn);
        usersTable.getColumns().add(DOBColumn);
        usersTable.getColumns().add(genderColumn);
        usersTable.getColumns().add(emailColumn);

        userNameColumn.setMinWidth(160.0);
        nameColumn.setMinWidth(160.0);
        addressColumn.setMinWidth(160.0);
        DOBColumn.setMinWidth(160.0);
        genderColumn.setMinWidth(160.0);
        emailColumn.setMinWidth(160.0);

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

        //usersTable.getItems().add(new UserClone("User2","123","2nd",Date.valueOf("2020-12-05"),true,"user2@gmail.com"));
        //usersTable.getItems().add(new UserClone("User3","123","3th",Date.valueOf("2020-12-05"),true,"user3@gmail.com"));
        //usersTable.getItems().add(new UserClone("User4","123","4th",Date.valueOf("2020-12-05"),true,"user4@gmail.com"));

    }

}
