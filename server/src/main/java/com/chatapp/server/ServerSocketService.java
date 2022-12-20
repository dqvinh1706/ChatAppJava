package com.chatapp.server;

import com.chatapp.commons.models.User;
import com.chatapp.commons.request.Request;
import com.chatapp.commons.response.Response;
import com.chatapp.server.handlers.AuthHandler;
import com.chatapp.server.handlers.ClientHandler;
import com.chatapp.server.handlers.UserHandler;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerSocketService extends Service {
    private final int PORT = 5050;
    private ServerController serverController;
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clientHandlers;

    public ServerSocketService(ServerController serverController) {
        this.serverController = serverController;
        this.clientHandlers = new HashMap<>();
    }

    public void close() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try{
                    serverSocket = new ServerSocket(PORT);
                    Platform.runLater(() -> {
                        serverController.setWelcomeText(
                                "Server is listening at " +
                                        serverSocket.getInetAddress().getHostName() +
                                        " on "
                                        + serverSocket.getLocalPort());
                    });
                    while(true) {
                        Socket socket = serverSocket.accept();
                        ClientHandler clientHandler = new AuthHandler(socket, clientHandlers);
                        clientHandler.start();
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                   close();
                }
                return null;
            }
        };
    }
}
