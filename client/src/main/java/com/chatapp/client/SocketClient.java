package com.chatapp.client;

import com.chatapp.commons.request.Request;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.Synchronized;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.Socket;

public class SocketClient {
    private Socket socketClient;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private static volatile SocketClient INSTANCE;

    public final void renewStream() throws IOException {
        this.out = new ObjectOutputStream(socketClient.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socketClient.getInputStream());
    }

    @Synchronized
    public final static SocketClient getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new SocketClient();
            System.out.println("new INSTANCE");
            System.out.println(INSTANCE);
        }
        return INSTANCE;
    }


    public final void sendRequest(Request req) throws IOException {
        this.out.writeObject(req);
        this.out.flush();
    }

    @Synchronized
    public final Object getResponse() throws IOException, ClassNotFoundException {
        return this.in.readObject();
    }

    private SocketClient() throws IOException {
        try {
            this.socketClient = new Socket("localhost", 5050);
            this.out = new ObjectOutputStream(socketClient.getOutputStream());
            this.in = new ObjectInputStream(socketClient.getInputStream());
        }
        catch (IOException err) {
            close();
            throw err;
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
            INSTANCE = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
