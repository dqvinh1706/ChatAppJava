package com.chatapp.client.workers;

import com.chatapp.client.SocketClient;
import com.chatapp.commons.request.Request;
import com.chatapp.commons.response.Response;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    protected SimpleBooleanProperty isAlive = new SimpleBooleanProperty();
    public SocketService(@NonNull SocketClient socketClient) {
        this.socketClient = socketClient;
        isAlive.bind(Bindings.notEqual(stateProperty(), Worker.State.CANCELLED));
    }

    @Synchronized
    public void addRequest(Request req) {
        try{
            // Prevent duplicate request
            if (previousReq != null && req.equals(previousReq)) {
                if (lastReqTime != null && System.currentTimeMillis() - lastReqTime <= 200) return;
            }
            reqQueue.put(req);
            lastReqTime = System.currentTimeMillis();
            previousReq = req;
        }catch (InterruptedException err) {
            err.printStackTrace();
        }
    }

    @Synchronized
    public Object getResponse() throws InterruptedException {
        return resQueue.take();
    }

    protected void listenRequest() {
        try{
            while(isAlive.get()) {
                Request req = reqQueue.take();
                socketClient.sendRequest(req);
            }
        }catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try{
                    new ResponseProcess().start();
                    listenRequest();
                }catch (Exception err) {
                    err.printStackTrace();
                }
                return null;
            }
        };
    }

    protected abstract void listenResponse();
    private class ResponseProcess extends Service {

        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    listenResponse();
                    return null;
                }
            };
        }
    }
}
