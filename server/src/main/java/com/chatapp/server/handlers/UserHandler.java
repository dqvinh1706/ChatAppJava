package com.chatapp.server.handlers;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.models.Message;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.*;
import com.chatapp.commons.response.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class UserHandler extends ClientHandler {
    @Setter
    private User loggedUser;
    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        clientHandlers.put(loggedUser.getUsername(), this);
    }

    public UserHandler(Socket socket, Map<String, ClientHandler> clientHandlers) throws IOException {
        super(socket, clientHandlers);
    }

    private void handlerConversationRequest(ConversationRequest req) throws IOException, InterruptedException {
        switch (req.getAction()) {
            case CREATE_CONVERSATION:
                User friend = userService.getUserById((Integer) req.getBody());
                int conId = conversationService.saveConversation(
                        new Conversation(friend.getFullName(), loggedUser.getId()),
                        List.of(friend.getId(), loggedUser.getId())
                );

                sendResponse(
                        ConversationResponse.builder()
                                .statusCode(StatusCode.OK)
                                .conversation(
                                        Conversation.builder().id(conId).build())
                                .build()
                );
                break;
            case GET_ALL_CONVERSATION:
                List<Conversation> conversationList = conversationService.getAllConversationOfUser(loggedUser.getId());
                Map<Conversation, Message> result = null;
                if (conversationList != null) {
                    result = conversationList
                            .stream()
                            .collect(Collectors.toMap(
                                    conversation -> conversation,
                                    conversation -> messageService.getNewestMessage(conversation.getId())
                            ));
                }
                sendResponse(
                        ConversationListResponse.builder()
                                .statusCode(conversationList == null ? StatusCode.BAD_REQUEST : StatusCode.OK)
                                .conversations(result)
                                .build()
                );
                break;
            case GET_ONE_CONVERSATION:
                Integer friendId = (Integer) req.getBody();
                Conversation con = conversationService.getOneOneConversation(
                       loggedUser.getId(), friendId
                );

                sendResponse(
                        ConversationResponse.builder()
                                .statusCode(con == null ? StatusCode.BAD_REQUEST : StatusCode.OK)
                                .conversation(con)
                                .build()
                );
                break;
            case DELETE_CONVERSATION:
                conversationService.deleteConversation((int) req.getBody(), loggedUser.getId());
                break;
        }
    }

    public void handleFriendRequest(FriendRequest req) throws IOException {
        switch (req.getAction()) {
            case ADD_FRIEND:
                userService.acceptFriendRequest(loggedUser.getId(), (int) req.getBody());
                break;
            case REMOVE_ADD_FRIEND:
                userService.cancelFriendRequest(loggedUser.getId(), (int) req.getBody());
                break;
            case GET_ALL_FRIENDS:
                List<User> users = userService.getAllFriends(loggedUser.getId());
                System.out.println(users);
                sendResponse(
                        FriendListResponse.builder()
                                .statusCode(users == null ? StatusCode.BAD_REQUEST : StatusCode.OK)
                                .title("Mọi người")
                                .friendList(users)
                                .build()
                );
                break;
            case GET_PENDING_ADD_FRIEND:
                List<User> pendingFriends = userService.getPendingFriends(loggedUser.getId());
                sendResponse(
                        FriendListResponse.builder()
                                .statusCode(pendingFriends == null ? StatusCode.BAD_REQUEST : StatusCode.OK)
                                .title("Lời mời kết bạn")
                                .friendList(pendingFriends)
                                .build()
                );
                break;
        }
    }

    public void handleMessageRequest(MessageRequest req) throws IOException {
        switch (req.getAction()) {
            case GET_CHAT_HISTORY:
                Properties body = (Properties) req.getBody();
                List<Message> messageList = messageService.getMessageFromConversation(
                        (int) body.get("conId"),
                        loggedUser.getId(),
                        (int) body.get("offset"),
                        (int) body.get("limit"));
                sendResponse(
                        ChatHistoryResponse.builder()
                                .statusCode(messageList == null ? StatusCode.BAD_REQUEST : StatusCode.OK)
                                .messageList(messageList)
                                .build()
                );
                break;
            case SEND_MESSAGE:
                Message message = (Message) req.getBody();
                try {
                    int messageId = messageService.saveMessage(message);
                    List<User> users = userService.getUsersInConversation(message.getConversationId());
                    users.parallelStream().forEach(user -> {
                        ClientHandler clientHandler = clientHandlers.get(user.getUsername());
                        if (clientHandler == null) return;
                        try {
                            clientHandler.sendResponse(
                                    MessageResponse.builder()
                                            .statusCode(StatusCode.OK)
                                            .message(message)
                                            .build()
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                }catch (Exception err) {
                    sendResponse(
                            MessageResponse.builder()
                                    .statusCode(StatusCode.BAD_REQUEST)
                                    .build()
                    );
                }
                break;
        }
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try{
                    while (true) {
                        System.out.println("Wait user req");
                        Object input = receiveRequest();
                        System.out.println(input);
                        if (ObjectUtils.isEmpty(input))
                            continue;

                        Request request = (Request) input;
                        if (request instanceof ConversationRequest) {
                            handlerConversationRequest((ConversationRequest) request);
                        }
                        else if (request instanceof FriendRequest) {
                            handleFriendRequest((FriendRequest) request);
                        }
                        else if (request instanceof MessageRequest) {
                            handleMessageRequest((MessageRequest) request);
                        }
                    }
                }catch (IOException | ClassNotFoundException e){
                    close();
                    clientHandlers.remove(loggedUser.getUsername());
                    e.printStackTrace();
                }catch (Exception err) {
                    err.printStackTrace();
                }
                return null;
            }
        };
    }
}
