package com.chatapp.client.controllers;

import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.ActionResponse;
import com.chatapp.commons.response.AllUsersResponse;
import com.chatapp.commons.response.Response;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    private int SelectedID = -1;
    @FXML
    private AnchorPane ChangePassword;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label currentPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private Label notificationText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
    }
    @FXML
    void ChangePassword(ActionEvent event){
        if (!confirmPassword.getText().equals(newPassword.getText())){
            notificationText.setText("Confirm password is difference with new password.");
            return;
        }
        Alert confirmChangePassword = new Alert(Alert.AlertType.CONFIRMATION);
        confirmChangePassword.setTitle("Change password confirm");
        confirmChangePassword.setHeaderText(null);
        confirmChangePassword.setContentText("Do you really want to change password?");
        Optional<ButtonType> option = confirmChangePassword.showAndWait();

        if (option.get() == ButtonType.OK) {
            if (!userSocketService.isRunning()) userSocketService.start();
            Task waitResponse = new Task() {
                @Override
                protected Response call() throws Exception {
                    User userAndPassword = new User(SelectedID, newPassword.getText());
                    userSocketService.addRequest(
                            ManageUsersRequest.builder()
                                    .action(Action.CHANGE_PASSWORD)
                                    .body(userAndPassword)
                                    .build()
                    );
                    return (Response) userSocketService.getResponse();
                }
            };

            waitResponse.setOnSucceeded(e -> {
                ActionResponse res = (ActionResponse) waitResponse.getValue();
                notificationText.setText(res.getNotification());
            });

            Thread th = new Thread(waitResponse);
            th.setDaemon(true);
            th.start();
        }
    }

    private void getPassword(){
        if(!userSocketService.isRunning()) userSocketService.start();
        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.GET_PASSWORD_BY_ID)
                                .body(SelectedID)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };
        waitResponse.setOnSucceeded(e -> {
            ActionResponse res = (ActionResponse) waitResponse.getValue();
            this.currentPassword.setText(res.getNotification());
        });
        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();
    }
    public void setValue(int SelectedId){
        this.SelectedID = SelectedId;
        getPassword();
    }
}
