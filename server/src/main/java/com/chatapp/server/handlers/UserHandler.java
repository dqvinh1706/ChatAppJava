package com.chatapp.server.handlers;

import com.chatapp.commons.enums.Action;
import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.models.Message;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.*;
import com.chatapp.commons.response.*;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class UserHandler extends ClientHandler {
    @Setter
    private User loggedUser = new User();

    public UserHandler(Socket socket, Map<String, ClientHandler> clientHandlers) throws IOException {
        super(socket, clientHandlers);
        loggedUser = userService.getUserById(5);
        System.out.println(loggedUser.getId());
//        clientHandlers.put("1", this);
    }

    private void handlerConversationRequest(ConversationRequest req) throws IOException {
        switch (req.getAction()) {
            case CREATE_CONVERSATION: {
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
            }
            case CREATE_GROUP_CHAT: {
                int creatorId = loggedUser.getId();
                List<Integer> membersId = (List<Integer>) req.getBody();
                membersId.add(creatorId);
                List<String> listName = new ArrayList<>();
                for (int i = 0; i < membersId.size(); i++) {
                    if (i == 3) break;
                    User member = userService.getUserById(membersId.get(i));
                    List<String> splitName = List.of(member.getFullName().split(" "));
                    String lastName = splitName.get(splitName.size() - 1);
                    listName.add(lastName);
                }
                String grName = "Nhóm của " +
                        String.join(", ", listName) +
                        (membersId.size() > 3 ? "..." : "");
                Conversation con = new Conversation(grName, loggedUser.getId());
                con.setIsGroup(true);
                int conId = conversationService.saveConversation(
                        con ,
                        membersId
                );
                Message greeting = new Message();
                greeting.setSenderId(loggedUser.getId());
                greeting.setConversationId(conId);
                greeting.setMessage(grName + " đã được tạo");

                try{
                    messageService.saveMessage(greeting);
                    sendResponse(
                            ConversationResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .conversation(
                                            Conversation.builder().id(conId).build())
                                    .build()
                    );
                }catch (Exception err) {
                    sendResponse(
                            ConversationResponse.builder()
                                    .statusCode(StatusCode.BAD_REQUEST)
                                    .build()
                    );
                }
                break;
            }
            case CHANGE_TITLE: {
                Properties body = (Properties) req.getBody();
                String newTitle = (String) body.get("newTitle");
                Integer conId = (Integer) body.get("conId");
                conversationService.updateTitle(conId, newTitle);
                break;
            }

            case GET_ALL_CONVERSATION: {
                List<Conversation> conversationList = conversationService.getAllConversationOfUser(loggedUser.getId());
                Map<Conversation, Message> result = conversationList
                        .stream()
                        .collect(Collectors.toMap(
                                conversation -> conversation,
                                conversation -> messageService.getNewestMessage(conversation.getId())
                        ));
                sendResponse(
                        ConversationListResponse.builder()
                                .statusCode(conversationList == null ? StatusCode.BAD_REQUEST : StatusCode.OK)
                                .conversations(result)
                                .build()
                );
                break;
            }
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
//                userService.acceptFriendRequest(loggedUser.getId(), (int) req.getBody());
                break;
            case REMOVE_ADD_FRIEND:
                userService.cancelFriendRequest(loggedUser.getId(), (int) req.getBody());
                break;
            case GET_ALL_FRIENDS:
                List<User> users = userService.getAllFriends(loggedUser.getId());
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

    public void handleSearchRequest(SearchRequest req) throws IOException {
        switch (req.getAction()) {
            case SEARCH_FRIEND:
                Properties params = req.getParams();
                String keyword = (String) params.get("keyword");
                User user = userService.getUserByUsername(keyword);
                System.out.println(keyword);

                // Trùng với bản thân
                if (user != null && user.getId() == loggedUser.getId())
                    user = null;
                sendResponse(
                        SearchResponse.builder()
                                .statusCode(user != null ? StatusCode.OK : StatusCode.BAD_REQUEST)
                                .result(user)
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
                        else if (request instanceof SearchRequest) {
                            handleSearchRequest((SearchRequest) request);
                        }
                    }
                }catch (IOException | ClassNotFoundException e){
                    close();
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
