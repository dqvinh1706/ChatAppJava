package com.chatapp.client.workers;

import com.chatapp.client.SocketClient;
import com.chatapp.commons.response.AuthResponse;
import com.chatapp.commons.response.Response;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import lombok.NonNull;
import lombok.Synchronized;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;

public class AuthSocketService extends SocketService {
    private static volatile AuthSocketService INSTANCE;

    @Synchronized
    public static AuthSocketService getInstance(@NonNull SocketClient socketClient) {
        if (INSTANCE == null) {
            INSTANCE = new AuthSocketService(socketClient);
        }
        if (!INSTANCE.isRunning())
            INSTANCE.start();
        return INSTANCE;
    }

    @Synchronized
    public static AuthSocketService getInstance() {
        if (INSTANCE == null) throw new NullPointerException("User Socket Service was not defined");
        return INSTANCE;
    }

    private AuthSocketService(@NonNull SocketClient socketClient) {
        super(socketClient);
    }

    @Override
    protected void listenResponse() {
        try {
            while (isAlive.get()) {
                Object input = socketClient.getResponse();
                if (ObjectUtils.isEmpty(input)) {
                    continue;
                }
                resQueue.put((Response) input);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception err) {
            err.printStackTrace();
        }
    }
}
