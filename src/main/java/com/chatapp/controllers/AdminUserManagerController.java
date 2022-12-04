package com.chatapp.controllers;

import com.chatapp.models.User;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class AdminUserManagerController implements Initializable {

    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView<User> usersTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        TableColumn<User, String> userNameColumn = new TableColumn<User, String>("User Name");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("username"));

        TableColumn<User, String> nameColumn = new TableColumn<User, String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));

        TableColumn<User, String> addressColumn = new TableColumn<User, String>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<User, String>("address"));

        TableColumn<User, Date> DOBColumn = new TableColumn<User, Date>("Birthday");
        DOBColumn.setCellValueFactory(new PropertyValueFactory<User, Date>("DOB"));

        TableColumn<User, Boolean> genderColumn = new TableColumn<User, Boolean>("User Name");
        genderColumn.setCellValueFactory(new PropertyValueFactory<User, Boolean>("gender"));

        TableColumn<User, String> emailColumn = new TableColumn<User, String>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));

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

        //usersTable.getItems().add(new User("User2","123","2nd",Date.valueOf("2020-12-05"),true,"user2@gmail.com"));
        //usersTable.getItems().add(new User("User3","123","3th",Date.valueOf("2020-12-05"),true,"user3@gmail.com"));
        //usersTable.getItems().add(new User("User4","123","4th",Date.valueOf("2020-12-05"),true,"user4@gmail.com"));

    }
}
