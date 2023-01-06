package com.chatapp.server.handlers;

import com.chatapp.commons.models.User;
import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.request.AuthRequest;
import com.chatapp.commons.response.AuthResponse;
import com.chatapp.commons.utils.PasswordUtil;
import com.chatapp.server.services.UserService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

@Getter
public class AuthHandler extends ClientHandler {
    private final UserService userService = UserService.getInstance();
    private User user = null;

    private Exception errorText = null;
    private StatusCode isAuthenticated = StatusCode.UNAUTHENTICATED;

    private void resetData() {
        errorText = null;
        user = null;
        isAuthenticated = null;
    }

    public AuthHandler(Socket socket, Map<String, ClientHandler> clientHandlers) throws IOException {
        super(socket, clientHandlers);
    }

    private void checkAuthenticate() {

        if (isAuthenticated == null) return;
        try {
            if (isAuthenticated.equals(StatusCode.AUTHENTICATED)) {
                clientHandlers.put(user.getUsername(), rolePermission(user));
                succeeded();
                return;
            }
            sendResponse(
                    AuthResponse.builder()
                            .statusCode(isAuthenticated)
                            .err(errorText)
                            .user(user)
                            .build()
            );
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void login(AuthRequest req) throws IOException {
        Properties data = req.getFormData();
        System.out.println(req);
        String username = (String) data.get("username");
        String rawPassword = (String) data.get("password");
        try{
            user = userService.getUserByUsername(username);

            if (user == null || !PasswordUtil.checkMatch(rawPassword, user.getPassword())) {
                errorText = new Exception("Username or password is incorrect");
                isAuthenticated = StatusCode.UNAUTHENTICATED;
            }
            else{
                isAuthenticated = StatusCode.AUTHENTICATED;
            }
        }
        catch (Exception err){
            err.printStackTrace();
        }
    }

    public void signup(AuthRequest req) throws IOException {
        Properties data = req.getFormData();
        String username = (String) data.get("username");
        String rawPassword = (String) data.get("rawPassword");
        String email = (String) data.get("email");

        user = userService.getUserByUsername(username);
        if (user != null) {
            isAuthenticated = StatusCode.UNAUTHENTICATED;
        }

    }
    private ClientHandler rolePermission(User user) throws IOException {
        if (user.getIsAdmin()) {
            // Admin role handler
            return null;
        }
        // User role handler
        UserHandler userHandler = new UserHandler(getClientSocket(), clientHandlers);
        userHandler.setLoggedUser(user);
        return userHandler;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try{
                    while (!isCancelled()) {
                        Object input = receiveRequest();
                        if (ObjectUtils.isNotEmpty(input)) {
                            AuthRequest req = (AuthRequest) input;
                            switch (req.getAction()) {
                                case LOGIN:
                                    login(req);
                                    break;
                                case SIGNUP:
                                    break;
                                case FORGOT_PASSWORD:
                                    break;
                                case DISCONNECT: {
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                        checkAuthenticate();
                        resetData();
                    }

                }catch (IOException | ClassNotFoundException e){
                    close();
                    e.printStackTrace();
                    System.out.println(user.getUsername() + " is disconnected");
                }

                return null;
            }
        };
    }
}
