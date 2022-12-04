package com.chatapp.controllers;

import com.chatapp.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class AdminLoginListController implements Initializable {

    @FXML
    private TableView<User> LoginList;

    @Override

    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<User, String> userNameColumn = new TableColumn<User, String>("User Name");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("username"));

        TableColumn<User, String> nameColumn = new TableColumn<User, String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));

        TableColumn<User, Date> updateColumn = new TableColumn<User, Date>("Last Login");
        updateColumn.setCellValueFactory(new PropertyValueFactory<User, Date>("lastLogin"));

        LoginList.getColumns().add(userNameColumn);
        LoginList.getColumns().add(nameColumn);
        LoginList.getColumns().add(updateColumn);

        userNameColumn.setMinWidth(160.0);
        nameColumn.setMinWidth(160.0);
        updateColumn.setMinWidth(160.0);
    }
}
