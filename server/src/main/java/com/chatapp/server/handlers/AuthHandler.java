package com.chatapp.server.handlers;

import com.chatapp.commons.models.User;
import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.request.AuthRequest;
import com.chatapp.commons.response.AuthResponse;
import com.chatapp.commons.response.Response;
import com.chatapp.commons.utils.PasswordUtil;
import com.chatapp.server.services.UserService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Calendar;
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
        isAuthenticated = StatusCode.UNAUTHENTICATED;
    }

    public AuthHandler(Socket socket, Map<String, ClientHandler> clientHandlers) throws IOException {
        super(socket, clientHandlers);
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.in = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void checkAuthenticate() {
        if (isAuthenticated == null) return;
        try {
            sendResponse(
                    AuthResponse.builder()
                            .statusCode(isAuthenticated)
                            .err(errorText)
                            .user(user)
                            .build()
            );

            if (isAuthenticated.equals(StatusCode.AUTHENTICATED)) {
                this.clientHandlers.put(user.getUsername(), rolePermission(user));
                Platform.runLater(() -> {
                    cancel();
                });
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void login(AuthRequest req) throws IOException {
        Properties data = req.getFormData();
        String username = (String) data.get("username");
        String rawPassword = (String) data.get("password");
        try {
            user = userService.getUserByUsername(username);
            if (user == null || !PasswordUtil.checkMatch(rawPassword, user.getPassword())) {
                errorText = new Exception("Username or password is incorrect");
                isAuthenticated = StatusCode.UNAUTHENTICATED;
            } else {
                if (clientHandlers.get(user.getUsername()) != null) {
                    isAuthenticated = StatusCode.UNAUTHENTICATED;
                    errorText = new Exception("Account already logged");
                }
                else if (user.getIsBlocked()) {
                    isAuthenticated = StatusCode.UNAUTHENTICATED;
                    errorText = new Exception("Account blocked");
                }
                else {
                    isAuthenticated = StatusCode.AUTHENTICATED;
                    user.setIsActive(true);
                    userService.updateUser(user);
                    userService.setLogin(user);
                }
            }
        } catch (Exception err) {
            errorText = new Exception("Username or password is incorrect");
            isAuthenticated = StatusCode.UNAUTHENTICATED;
        }
    }

    public void signUp(AuthRequest req) throws IOException {
        Properties data = req.getFormData();
        String username = (String) data.get("username");
        String rawPassword = (String) data.get("rawPassword");
        String email = (String) data.get("email");
        try {
            user = userService.getUserByUsername(username);
            if (user != null) {
                isAuthenticated = StatusCode.UNAUTHENTICATED;
            } else if (userService.signUp(username, rawPassword, email)) {
                isAuthenticated = StatusCode.AUTHENTICATED;
                user = null;
            } else {
                isAuthenticated = StatusCode.UNAUTHENTICATED;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private ClientHandler rolePermission(User user) throws IOException {
        try {
            ClientHandler clientHandler = null;
            if (user.getIsAdmin()) {
                // Admin role handler
                clientHandler = new AdminHandler(getClientSocket(), clientHandlers);
                clientHandler.setIn(this.in);
                clientHandler.setOut(this.out);
                clientHandler.start();
                System.out.println("Run admin");
                return clientHandler;
            }
            // User role handler
            UserHandler userHandler = new UserHandler(getClientSocket(), clientHandlers);
            userHandler.setIn(this.in);
            userHandler.setOut(this.out);

            userHandler.setLoggedUser(user);
            userHandler.start();
            return userHandler;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return null;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try {
                    while (!isCancelled()) {
                        System.out.println(isAuthenticated);
                        if (isAuthenticated != null && isAuthenticated == StatusCode.AUTHENTICATED && user != null) {
                            cancel();
                            break;
                        } else {
                            resetData();
                        }

                        Object input = receiveRequest();
                        if (ObjectUtils.isNotEmpty(input)) {
                            AuthRequest req = (AuthRequest) input;
                            switch (req.getAction()) {
                                case LOGIN:
                                    login(req);
                                    break;
                                case SIGNUP:
                                    signUp(req);
                                    break;
                                case FORGOT_PASSWORD:
                                    break;
                                default:
                                    break;
                            }
                            checkAuthenticate();
                        }
                    }


                } catch (IOException | ClassNotFoundException e) {
                    close();
                    e.printStackTrace();
                    System.out.println(user.getUsername() + " is disconnected");
                }
                return null;
            }
        };
    }
}
