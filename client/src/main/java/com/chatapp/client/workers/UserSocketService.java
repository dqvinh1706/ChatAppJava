package com.chatapp.client.workers;

import com.chatapp.client.SocketClient;
import com.chatapp.commons.models.User;
import com.chatapp.commons.response.Response;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;

public class UserSocketService extends SocketService {
    private volatile static UserSocketService INSTANCE = null;

    @Synchronized
    public static UserSocketService getInstance(@NonNull SocketClient socketClient) {
        if (INSTANCE == null) {
            INSTANCE = new UserSocketService(socketClient);
        }

        return INSTANCE;
    }

    @Override
    public void close() {
        super.close();
        INSTANCE = null;
    }

    @Synchronized
    public static UserSocketService getInstance() {
        if (INSTANCE == null) throw new NullPointerException("User Socket Service was not defined");
        return INSTANCE;
    }

    @Getter @Setter
    private User loggedUser;

    private UserSocketService(@NonNull SocketClient socketClient) {
        super(socketClient);
    }

    @Override
    protected void listenResponse() {
        try {
            while (isAlive.get()) {
                System.out.println("wait response");
                Object object = socketClient.getResponse();
                if (ObjectUtils.isEmpty(object)) {
                    continue;
                }
                resQueue.put((Response) object);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception err) {
            err.printStackTrace();
        }
    }
}
