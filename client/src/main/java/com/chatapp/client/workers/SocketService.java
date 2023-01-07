package com.chatapp.client.workers;

import com.chatapp.client.SocketClient;
import com.chatapp.commons.request.Request;
import com.chatapp.commons.response.Response;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public abstract class SocketService extends Service {
    private Request previousReq = null;
    private Long lastReqTime = null;
    @NonNull
    protected SocketClient socketClient;
    protected final LinkedBlockingQueue<Request> reqQueue = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Response> resQueue = new LinkedBlockingQueue<>();

    protected final SimpleBooleanProperty isAlive = new SimpleBooleanProperty();

    public SocketService(@NonNull SocketClient socketClient) {
        this.socketClient = socketClient;
        isAlive.bind(Bindings.notEqual(stateProperty(), State.CANCELLED));
    }

    public void addRequest(Request req) {
        try {
            // Prevent duplicate request
            if (previousReq != null && req.equals(previousReq)) {
                if (lastReqTime != null && System.currentTimeMillis() - lastReqTime <= 200) return;
            }
            reqQueue.put(req);
            lastReqTime = System.currentTimeMillis();
            previousReq = req;
        } catch (InterruptedException err) {
            err.printStackTrace();
        }
    }

    @Synchronized
    public Object getResponse() throws InterruptedException {
        return resQueue.take();
    }

    protected void listenRequest() throws InterruptedException, IOException {
        Request req = reqQueue.take();
        System.out.println(req);
        if (req == null) {
            this.cancel();
            return;
        }
        socketClient.sendRequest(req);
    };
    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Void call() {
                try {
                    System.out.println("Start RES");
                    new ResponseProcess().start();
                    while (!isCancelled()){
                        System.out.println("WHILE req");
                        listenRequest();
                    }
                } catch (Exception err) {
                    cancel();
                    System.out.println("Cancel listener");
//                    err.printStackTrace();
                }
                return null;
            }
        };
    }

    protected abstract void listenResponse() throws Exception;

    private class ResponseProcess extends Service {

        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws InterruptedException {
                    try {
                        listenResponse();
                    } catch (Exception err) {
                    }
                    return null;
                }
            };
        }
    }
}
