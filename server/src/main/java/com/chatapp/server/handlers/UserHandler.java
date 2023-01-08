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
    private User loggedUser;

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        clientHandlers.put(loggedUser.getUsername(), this);
    }

    public UserHandler(Socket socket, Map<String, ClientHandler> clientHandlers) throws IOException {
        super(socket, clientHandlers);
    }

    private void handlerConversationRequest(ConversationRequest req) throws IOException {
        switch (req.getAction()) {
            case CREATE_CONVERSATION: {
                User friend = userService.getUserById((Integer) req.getBody());
                Conversation con = new Conversation(friend.getFullName(), loggedUser.getId());
                con.setIsGroup(false);
                int conId = conversationService.saveConversation(
                        con,
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
                        con,
                        membersId
                );
                Message greeting = new Message();
                greeting.setSenderId(loggedUser.getId());
                greeting.setConversationId(conId);
                greeting.setMessage(grName + " đã được tạo");

                try {
                    messageService.saveMessage(greeting);
                    sendResponse(
                            ConversationResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .conversation(
                                            Conversation.builder().id(conId).build())
                                    .build()
                    );
                } catch (Exception err) {
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
            case GET_MEMBERS: {
                Properties body = (Properties) req.getBody();
                List<User> users = userService.getUsersInConversation((Integer) body.get("conId"));
                List<User> admins = userService.getAdminFromGroup((Integer) body.get("conId"));

                users = users.parallelStream().map(user -> {
                    return admins.parallelStream().anyMatch(admin -> admin.getId() == user.getId()) ? null : user;
                }).collect(Collectors.toList());
                System.out.println(users);
                System.out.println(admins);

                body = new Properties();
                body.put("members", users);
                body.put("admins", admins);

                sendResponse(
                        InformationResponse.builder()
                                .statusCode(StatusCode.OK)
                                .forAction(Action.GET_MEMBERS)
                                .body(body)
                                ._class(req.getClass())
                                .build()
                );
                break;
            }
            case ADD_MEMBER: {
                Properties body = (Properties) req.getBody();
                Integer userId = (Integer) body.get("userId");
                Integer conId = (Integer) body.get("conId");
                conversationService.addMember(conId, userId);
                break;
            }

            case CHANGE_ADMIN: {
                Properties body = (Properties) req.getBody();
                Integer userId = (Integer) body.get("userId");
                Integer conId = (Integer) body.get("conId");
                conversationService.saveAdmin(conId, userId);
                break;
            }
            case KICK_MEMBER: {
                Properties body = (Properties) req.getBody();
                Integer userId = (Integer) body.get("userId");
                Integer conId = (Integer) body.get("conId");
                conversationService.deleteMember(conId, userId);
                break;
            }
            case GET_ALL_CONVERSATION: {
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
                userService.acceptFriendRequest(loggedUser.getId(), (int) req.getBody());
                break;
            case REMOVE_ADD_FRIEND:
                userService.cancelFriendRequest(loggedUser.getId(), (int) req.getBody());
                break;
            case UNFRIEND:
                userService.unfriend(loggedUser.getId(), (int) req.getBody());
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
            case ADD_PENDING_FRIEND:
                int result = userService.addToPendingFriend(loggedUser.getId(), (int) req.getBody());
                System.out.println(result);
                break;
        }
    }

    public void handleSearchRequest(SearchRequest req) throws IOException {
        switch (req.getAction()) {
            case SEARCH_FRIEND: {
                Properties params = req.getParams();
                String keyword = (String) params.get("keyword");
                List<User> users = userService.getAllFriends(loggedUser.getId());
                System.out.println(users);
                if (users != null) {
                    for (User user :
                            users) {
                        if (user.getUsername().equals(keyword)) {
                            System.out.println(user);
                            sendResponse(SearchResponse.builder()
                                    .statusCode(StatusCode.OK)
                                    .result(user)
                                    ._class(req.get_class())
                                    .build());
                            return;
                        }
                    }
                }
                sendResponse(SearchResponse.builder()
                        ._class(req.get_class())
                        .statusCode(StatusCode.BAD_REQUEST)
                        .result(null)
                        .build());
                break;
            }
            case SEARCH_USER:
                Properties params = req.getParams();
                String keyword = (String) params.get("keyword");
                User user = userService.getUserByUsername(keyword);
                System.out.println(keyword);

                try {
                    List<User> frList = userService.getAllFriends(loggedUser.getId());
                    List<User> pendingList = userService.getPendingFriends(user.getId());
                    List<User> loggedUserPendingList = userService.getPendingFriends(loggedUser.getId());

                    if (user != null && (user.getId() == loggedUser.getId()))
                        user = null;
                    if (user != null && (frList != null && (frList.indexOf(user) != -1)))
                        user = null;
                    if (user != null && (pendingList != null && pendingList.stream().anyMatch((u) -> u.getId() == loggedUser.getId()))) {
                        user = null;
                    }
                    final User finalUser = user;
                    if (user != null && (loggedUserPendingList != null && loggedUserPendingList.stream().anyMatch((u) -> u.getId() == finalUser.getId()))) {
                        user = null;
                    }


//                    if (user != null && (user.getId() == loggedUser.getId() ||
//                            (frList != null && frList.stream().anyMatch((u) -> u.getId() == finalUser.getId())) ||
//                            (pendingList != null && pendingList.stream().anyMatch((u) -> u.getId() == loggedUser.getId()))))
//                        user = null;
                    sendResponse(
                            SearchResponse.builder()
                                    .statusCode(user != null ? StatusCode.OK : StatusCode.BAD_REQUEST)
                                    .result(user)
                                    ._class(req.get_class())
                                    .build()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(
                            SearchResponse.builder()
                                    .statusCode(StatusCode.BAD_REQUEST)
                                    .result(user)
                                    ._class(req.get_class())
                                    .build());
                }

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

                List<String> senderNames = new ArrayList<>();
                if (messageList != null) {
                    for (int i = 0; i < messageList.size() - 1; i++) {
                        Message currMess = messageList.get(i);
                        Message nextMess = messageList.get(i + 1);
                        if (currMess.getSenderId() != nextMess.getSenderId()) {
                            if (nextMess.getSenderId() != loggedUser.getId()) {
                                System.out.println(userService.getUserById(nextMess.getSenderId()).getFullName());
                                senderNames.add(userService.getUserById(nextMess.getSenderId()).getFullName());
                            }
                        }
                    }
                    if (messageList.get(0).getSenderId() != loggedUser.getId()) {
                        senderNames.add(0, userService.getUserById(messageList.get(0).getSenderId()).getFullName());
                    }
                }
                sendResponse(
                        ChatHistoryResponse.builder()
                                .statusCode(messageList == null ? StatusCode.BAD_REQUEST : StatusCode.OK)
                                .messageList(messageList)
                                .senderNames(senderNames)
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
                                            .senderName(userService.getUserById(message.getSenderId()).getFullName())
                                            .message(message)
                                            .build()
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                } catch (Exception err) {
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
                try {
                    while (!isCancelled()) {
                        Object input = receiveRequest();
                        if (ObjectUtils.isEmpty(input))
                            continue;

                        Request request = (Request) input;
                        if (request instanceof ConversationRequest) {
                            handlerConversationRequest((ConversationRequest) request);
                        } else if (request instanceof FriendRequest) {
                            handleFriendRequest((FriendRequest) request);
                        } else if (request instanceof MessageRequest) {
                            handleMessageRequest((MessageRequest) request);
                        } else if (request instanceof SearchRequest) {
                            handleSearchRequest((SearchRequest) request);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    clientHandlers.remove(loggedUser.getUsername());
                    close();
                    e.printStackTrace();
                } catch (Exception err) {
                    err.printStackTrace();
                }
                return null;
            }
        };
    }
}
