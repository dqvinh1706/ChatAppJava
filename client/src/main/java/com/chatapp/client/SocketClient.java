package com.chatapp.client;

import com.chatapp.commons.request.Request;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.Synchronized;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class SocketClient {
    private Socket socketClient;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private static volatile SocketClient INSTANCE;

    @Synchronized
    public final static SocketClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SocketClient();
        }
        return INSTANCE;
    }

    public final void sendRequest(Request req) throws IOException {
        this.out.writeObject(req);
        this.out.flush();
    }

    public final Object getResponse() throws IOException, ClassNotFoundException {
        return this.in.readObject();
    }

    private SocketClient() {
        try {
            this.socketClient = new Socket("localhost", 5050);
            this.out = new ObjectOutputStream(socketClient.getOutputStream());
            this.in = new ObjectInputStream(socketClient.getInputStream());
        }
        catch (IOException err) {
            err.printStackTrace();
            // Lỗi không kết nối được tới server
            Platform.runLater(() -> {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Connection Error!");
                error.setContentText("Can't connect to server!!!");
                error.setHeaderText(null);
                error.show();
            });
            close();
        }
    }

    public void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socketClient != null) {
                socketClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
