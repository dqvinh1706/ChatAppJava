package com.chatapp.client.components.UserTabs;

import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ConversationRequest;
import com.chatapp.commons.request.FriendRequest;
import com.chatapp.commons.request.InformationRequest;
import com.chatapp.commons.response.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.util.Objects;

public class UserTabs extends StackPane {
    private TabLayout tab = new TabLayout();
    private UserSocketService socketService;
    private User loggedUser;

    public void loadData() {
        this.socketService = UserSocketService.getInstance();
        loggedUser = socketService.getLoggedUser();
        tab.loadData();
        new Thread(new ListenResponse()).start();
    }
    public UserTabs() {
        super();
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setAlignment(Pos.CENTER);
        this.setId("main-container");

        getChildren().add(tab);
    }

    public void initTab() {
        loadConversationTab();
    }

    public void loadConversationTab() {
        System.out.println("SEND LOAD CON");
        socketService.addRequest(ConversationRequest.builder()
                        .action(Action.GET_ALL_CONVERSATION)
                        .build()
        );
    }

    public void loadFriendTab() {
        socketService.addRequest(FriendRequest.builder()
                .action(Action.GET_ALL_FRIENDS)
                .build()
        );
    }

    public void loadPendingFriendsTab() {
        socketService.addRequest(
                FriendRequest.builder()
                        .action(Action.GET_PENDING_ADD_FRIEND)
                        .build()
        );
    }

    private class ListenResponse extends Task {
        @Override
        protected Void call() {
            try {
                while (!isCancelled()) {
                    Object input = socketService.getResponse();
                    System.out.println(input);
                    if (input instanceof FriendListResponse) {
                        FriendListResponse res = (FriendListResponse) input;
                        tab.loadFriendTab(res.getTitle(), res.getFriendList());
                    }
                    else if (input instanceof ConversationListResponse) {
                        ConversationListResponse res = (ConversationListResponse) input;
                        tab.loadConversationTab(res.getConversations());
                    }
                    else if (input instanceof ConversationResponse) {
                        ConversationResponse res = (ConversationResponse) input;
                        tab.setCurrConversationId(res.getConversation());
                    }
                    else if (input instanceof ChatHistoryResponse) {
                        ChatHistoryResponse res = (ChatHistoryResponse) input;
                        tab.loadChatHistory(res.getMessageList());
                    } else if (input instanceof MessageResponse) {
                        MessageResponse res = (MessageResponse) input;
                        tab.updateMessage(res.getMessage());
                    }

                    // Wait for GUI to load before receive new response
                    Thread.sleep(20);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            return null;
        }
    }
}
