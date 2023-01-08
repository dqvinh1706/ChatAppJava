package com.chatapp.client.controllers;

import com.chatapp.commons.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class AdminLoginListController implements Initializable {

<<<<<<< Updated upstream
    @FXML
    private TableView<User> LoginList;
=======
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
                    String createdAt = Date2String(loginHistory.getCreatedAt());
                    data.add(new AdminLoginListController.LoginHistoryClone(
                            loginHistory.getId(),
                            loginHistory.getUsername(),
                            loginHistory.getName(),
                            createdAt
                    ));
                }
            }
        });

        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();
    }
>>>>>>> Stashed changes

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
