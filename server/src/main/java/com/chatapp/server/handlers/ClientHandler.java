package com.chatapp.server.handlers;

import com.chatapp.commons.response.Response;
import com.chatapp.server.services.ConversationService;
import com.chatapp.server.services.LoginHistoryService;
import com.chatapp.server.services.MessageService;
import com.chatapp.server.services.UserService;
import javafx.concurrent.Service;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

@Getter
public abstract class ClientHandler extends Service {
    protected Socket clientSocket;

    @Setter @Getter
    protected ObjectInputStream in;
    @Setter @Getter
    protected ObjectOutputStream out;
    protected Map<String, ClientHandler> clientHandlers;
    protected ConversationService conversationService = ConversationService.getInstance();
    protected MessageService messageService = MessageService.getInstance();
    protected UserService userService = UserService.getInstance();
    protected LoginHistoryService loginHistoryService = LoginHistoryService.getInstance();

    public ClientHandler(Socket socket, Map<String, ClientHandler> clientHandlers) throws IOException {
        try {
            this.clientSocket = socket;
            this.clientHandlers = clientHandlers;
        }
//        }catch (IOException e){e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}
    }

    protected Object receiveRequest() throws IOException, ClassNotFoundException {
        return this.in.readObject();
    }

    @Synchronized
    protected void sendResponse(Response response) throws IOException {
        this.out.writeObject(response);
        this.out.flush();
    }

    protected void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
