package com.chatapp.server.handlers;


import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.*;
import com.chatapp.commons.response.AllUsersResponse;
import com.chatapp.commons.response.LoginListRespone;
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

            case GET_LOGIN_LIST:
                List<LoginHistory> loginList = loginHistoryService.getLoginList();

                sendResponse(
                        LoginListRespone.builder()
                                .statusCode(StatusCode.OK)
                                .loginList(loginList)
                                .build()
                );
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
