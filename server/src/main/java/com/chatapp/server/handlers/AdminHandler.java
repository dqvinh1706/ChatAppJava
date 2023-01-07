package com.chatapp.server.handlers;


import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.*;
import com.chatapp.commons.response.ActionResponse;
import com.chatapp.commons.response.AllUsersResponse;
import com.chatapp.commons.response.DeleteUserResponse;
import com.chatapp.commons.response.LoginListResponse;
import com.chatapp.server.services.LoginHistoryService;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

@Getter
public class AdminHandler extends ClientHandler{
    @Setter
    private User loggedUser;
    public AdminHandler(Socket socket, Map<String, ClientHandler> clientHandlers) throws IOException {
        super(socket, clientHandlers);
    }

    private void handleManageUsersRequest(ManageUsersRequest req) throws IOException{
        switch (req.getAction()){
            case GET_ALL_USERS:
                List<User> allUsers = userService.getAllUsers();

                sendResponse(
                        AllUsersResponse.builder()
                                .statusCode(StatusCode.OK)
                                .allUsers(allUsers)
                                .build()
                );
                break;
            case ADD_NEW_USER:
                User newUser = (User) req.getBody();
                User findUser = null;
                findUser = userService.getUserByUsername(newUser.getUsername());
                if (findUser != null){
                    sendResponse(
                            ActionResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .notification("This username already have in system.")
                                    .build()
                    );
                    break;
                }

                Boolean addNewUserResult = userService.addNewUser(newUser);
                if (addNewUserResult) {
                    sendResponse(
                            ActionResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .notification("Add new user successfully.")
                                    .build()
                    );
                }
                else {
                    sendResponse(
                            ActionResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .notification("Having error in at new user.")
                                    .build()
                    );
                }
                break;

            case DELETE_USER:
                Boolean deleteUserResult = userService.deleteUserById((int) req.getBody());
                sendResponse(
                        DeleteUserResponse.builder()
                                .statusCode(StatusCode.OK)
                                .notification("Deleted successfully")
                                .build()
                );
                break;

            case GET_LOGIN_LIST:
                List<LoginHistory> loginList = loginHistoryService.getLoginList();

                sendResponse(
                        LoginListResponse.builder()
                                .statusCode(StatusCode.OK)
                                .loginList(loginList)
                                .build()
                );
                break;
            case CHANGE_PASSWORD:
                User userAndPassword = (User) req.getBody();
                Boolean resChangePassword = userService.changePassword(userAndPassword);
                System.out.println(resChangePassword);
                if (resChangePassword) {
                    sendResponse(
                            ActionResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .notification("Change password successfully.")
                                    .build()
                    );
                }
                else {
                    sendResponse(
                            ActionResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .notification("Having error when change password.")
                                    .build()
                    );
                }
                break;
        }
    }
    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try {
                    while (!isCancelled()) {
                        Object input = receiveRequest();
                        if (ObjectUtils.isEmpty(input))
                            continue;
                        System.out.println(input);
                        Request request = (Request) input;
                        if (request instanceof ManageUsersRequest) {
                            handleManageUsersRequest((ManageUsersRequest) request);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    close();
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
