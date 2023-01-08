package com.chatapp.client.components.UserTabs;

import com.chatapp.client.components.Avatar.Avatar;
import com.chatapp.client.components.ConversationBox.ConversationBox;
import com.chatapp.client.components.FriendBox.FriendBox;
import com.chatapp.client.components.FriendBox.PendingFriendBox;
import com.chatapp.client.components.GroupDialog.GroupDialog;
import com.chatapp.client.components.FriendBox.FriendBoxType;
import com.chatapp.client.components.FriendDialog.FriendDialog;
import com.chatapp.client.components.GroupMemberDialog.GroupMemberDialog;
import com.chatapp.client.components.MessageContainer.MessageContainer;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.models.Message;
import com.chatapp.commons.models.User;

import com.chatapp.commons.request.ConversationRequest;
import com.chatapp.commons.request.FriendRequest;
import com.chatapp.commons.request.MessageRequest;
import com.chatapp.commons.response.InformationResponse;
import com.chatapp.commons.utils.TimestampUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.io.IOException;
import java.util.*;

public class TabLayout extends GridPane {
    @Setter
    private int LIMIT = 15;
    @Setter
    @Getter
    private int offset = 0;
    @FXML
    private BorderPane chatContent;

    @FXML
    private ScrollPane chatScrollPane, tabScrollPane;

    @FXML
    private HBox chatSummaryContainer;
    @FXML
    private Button editChatTitle;
    @FXML
    private Label chatTitle, tabTitle;

    @FXML
    private TextField searchGlobal, searchMessageField, messageTextField;
    @FXML
    private VBox main, tabContent;

    @FXML
    private Button moreInfoBtn;

    private Avatar chatAvatar;
    protected final MessageContainer messageContainer = new MessageContainer();
    private final SimpleObjectProperty<Conversation> currConversation = new SimpleObjectProperty<>();
    protected int currUserId;
    protected Integer friendId = null;
    private UserSocketService socketService;

    private GroupDialog groupDialog;
    private GroupMemberDialog groupMemberDialog;

    private FriendDialog friendDialog = null;

    public TabLayout() {
        FXMLLoader loader = new FXMLLoader(TabLayout.class.getResource("TabLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            initGUI();
            setUpProperty();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void loadData() {
        socketService = UserSocketService.getInstance();
        currUserId = socketService.getLoggedUser().getId();
        messageContainer.setCurrUserId(currUserId);
    }

    private void setUpProperty() {
        chatAvatar.imageProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                chatAvatar.setImage(newValue);
            }
        }));

        editChatTitle.visibleProperty().bind(Bindings.equal(tabTitle.textProperty(), "Chat"));
        chatTitle.textProperty().addListener((obs, oldValue, newValue) -> {
            chatTitle.setTooltip(new Tooltip(newValue));
        });

        currConversation.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                messageContainer.clear();
                if (newValue == null) return;
                if (newValue.equals(oldValue)) return;
                offset = 0;
                sendChatHistoryRequest();
                moreInfoBtn.setVisible(newValue.getIsGroup());
                chatScrollPane.setVvalue(1.0f);
            });
        });

        chatScrollPane.setOnScroll(event -> {
            if (currConversation.getValue() == null) return;
            if (event.getDeltaY() > 0) {
                // Scroll up
                if (chatScrollPane.getVvalue() == 1.0f || chatScrollPane.getVvalue() <= 0.5) {
                    sendChatHistoryRequest();
                }
            }
        });

        tabScrollPane.getContent().setOnScroll(e -> {
            double deltaY = e.getDeltaY() * 2;
            double height = tabScrollPane.getContent().getBoundsInLocal().getHeight();
            tabScrollPane.setVvalue(tabScrollPane.getVvalue() - deltaY / height);
        });
    }

    public void setCurrConversation(Conversation con) {
//        this.currConversationId.set(con == null ? null : con.getId());
        this.currConversation.set(con == null ? null : con);
    }

    public void sendChatHistoryRequest() {
        Properties body = new Properties();
        body.put("conId", currConversation.getValue().getId());
        body.put("offset", offset);
        body.put("limit", LIMIT);

        socketService.addRequest(
                MessageRequest.builder()
                        .action(Action.GET_CHAT_HISTORY)
                        .body(body)
                        .build()
        );
    }

    private void initGUI() {
        chatAvatar = new Avatar(40, 40);
        chatAvatar.disabledActiveSymbol();
        chatSummaryContainer.getChildren().add(0, chatAvatar);
        chatContent.setCenter(messageContainer);
    }

    public void activeStyleToggle(Node ins) {
        Node oldActive = (Node) ins.getParent().lookup(".is-active");
        if (oldActive != null) {
            System.out.println(ins.getStyleClass());
            ins.getStyleClass().remove("unseen-message");

            if (oldActive.equals(ins)) {
                return;
            }
            oldActive.getStyleClass().remove("is-active");
        }
        ins.getStyleClass().add("is-active");
    }

    protected void setChatAvatarImg(Image img) {
        if (img == null) return;
        this.chatAvatar.setImage(img);
    }

    protected void setChatTitle(String title) {
        this.chatTitle.setText(title);
    }

    @FXML
    void onEditConversationName(ActionEvent event) {
        if (chatSummaryContainer.getChildren().contains(chatTitle)) {
            TextField newTitle = new TextField(chatTitle.getText());

            newTitle.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == false) {
                    chatSummaryContainer.getChildren().remove(newTitle);
                    chatSummaryContainer.getChildren().add(chatTitle);
                }
            });

            newTitle.setOnKeyReleased((e) -> {
                if (e.getCode() == KeyCode.ENTER) {
                    String title = newTitle.getText().trim();
                    chatTitle.setText(title);
                    System.out.println(tabContent.lookupAll("ConversationBox#" + currConversation.get()));
                    Properties body = new Properties();
                    body.put("newTitle", title);
                    body.put("conId", currConversation.get().getId());
                    socketService.addRequest(
                            ConversationRequest.builder()
                                    .action(Action.CHANGE_TITLE)
                                    .body(body)
                                    .build()
                    );
                    ConversationBox ins = (ConversationBox) tabContent.lookup("#ConversationBox#" + currConversation.getValue().getId());
                    ins.setTitle(title);
                    messageContainer.requestFocus();
                }
            });
            chatTitle.textProperty().bindBidirectional(newTitle.textProperty());
            chatSummaryContainer.getChildren().remove(chatTitle);
            chatSummaryContainer.getChildren().add(newTitle);
            newTitle.positionCaret(newTitle.getText().length());
            newTitle.requestFocus();
        }
    }

    @FXML
    void onAddFriendByUsername(ActionEvent event) throws IOException {
        friendDialog = new FriendDialog();
        friendDialog.show();
    }

    @FXML
    void onGroupMemberDialog() {
        groupMemberDialog = new GroupMemberDialog();
        groupMemberDialog.setUserId(currUserId);
        groupMemberDialog.setConversation(currConversation.get());
        groupMemberDialog.show();
    }

    @FXML
    void onCreateGrByUsername(ActionEvent event) {
        groupDialog = new GroupDialog();
        groupDialog.show();
    }

    @FXML
    private void onDeleteConversation(ActionEvent event) {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Xoá cuộc trò chuyện?");
        confirmDelete.setHeaderText(null);
        confirmDelete.setContentText("Bạn không thể hoàn tác sau khi xoá cuộc trò chuyện này.");
        Optional<ButtonType> option = confirmDelete.showAndWait();
        if (option.get() == ButtonType.OK) {
            if (currConversation.getValue() == null) {
                Alert errAlert = new Alert(Alert.AlertType.ERROR);
                errAlert.setTitle("Lỗi");
                errAlert.setHeaderText(null);
                errAlert.setContentText("Bạn chưa chọn cuộc trò chuyện");
                errAlert.showAndWait();
                return;
            }

            socketService.addRequest(
                    ConversationRequest.builder()
                            .action(Action.DELETE_CONVERSATION)
                            .body(currConversation.getValue())
                            .build()
            );

            // Update gui
            Platform.runLater(() ->
                    tabContent.getChildren().remove(tabContent.lookup("#ConversationBox#" + currConversation.getValue()))
            );
        }
    }

    @FXML
    private void onSendMessage(ActionEvent event) {
        String message = messageTextField.getText();
        messageTextField.clear();
        messageTextField.requestFocus();
        if (message.isBlank()) return;
        doSendMessage(message);
    }

    public void reload() {
        if (tabTitle.getText().equals("Chat")) {
            socketService.addRequest(ConversationRequest.builder()
                    .action(Action.GET_ALL_CONVERSATION)
                    .build()
            );
        } else if (tabTitle.getText().equals("Mọi người")) {
            socketService.addRequest(FriendRequest.builder()
                    .action(Action.GET_ALL_FRIENDS)
                    .build()
            );
        } else if (tabTitle.getText().equals("Lời mời kết bạn")) {
            socketService.addRequest(FriendRequest.builder()
                    .action(Action.GET_PENDING_ADD_FRIEND)
                    .build()
            );
        }
    }

    @Synchronized
    public void loadSearchResult(Class _class, Object result) {
        if (_class == GroupDialog.class) {
            if (groupDialog != null) {
                groupDialog.loadSearchResult(result);
            }
        } else if (_class == GroupMemberDialog.class) {
            if (groupMemberDialog == null) return;
            groupMemberDialog.loadSearchResult(result);
        } else if (_class == FriendDialog.class) {
            if (friendDialog == null) return;
            friendDialog.loadSearchResult((User) result);
        }
    }

    @Synchronized
    public void loadChatHistory(List<Message> messageList, List<String> senderNames) {
        if (messageList == null) return;
        offset += LIMIT;
        messageContainer.loadMessages(messageList, senderNames);
    }

    @Synchronized
    public void loadFriendTab(String title, List<User> users) {
        Platform.runLater(() -> {
            tabContent.getChildren().clear();
            tabTitle.setText(title);
            if (title.equals("Lời mời kết bạn")) {
                loadPendingFriendsTab(users);
                return;
            }
            if (users == null) return;
            users.forEach(friend -> {
                PendingFriendBox ins = PendingFriendBox.toPendingFriendBox(friend, FriendBoxType.UNFRIEND);
                ins.setOnMouseClicked(e -> {
                    onFriendBoxClicked(ins);
                });
                tabContent.getChildren().add(0, ins);
                ins.setCancelBtnAction((e) -> {
                    socketService.addRequest(
                            FriendRequest.builder()
                                    .action(Action.UNFRIEND)
                                    .body(ins.getUserId())
                                    .build()
                    );
                    tabContent.getChildren().remove(ins);
                });
            });
        });
    }

    private void loadPendingFriendsTab(List<User> users) {
        if (users == null) return;
        users.forEach(friend -> {
            PendingFriendBox ins = PendingFriendBox.toPendingFriendBox(friend);
            ins.setAcceptBtnAction((e) -> {
                socketService.addRequest(
                        FriendRequest.builder()
                                .action(Action.ADD_FRIEND)
                                .body(ins.getUserId())
                                .build()
                );
                tabContent.getChildren().remove(ins);
            });

            ins.setCancelBtnAction((e) -> {
                socketService.addRequest(
                        FriendRequest.builder()
                                .action(Action.REMOVE_ADD_FRIEND)
                                .body(ins.getUserId())
                                .build()
                );
                tabContent.getChildren().remove(ins);
            });

            tabContent.getChildren().add(0, ins);
        });
    }

    @Synchronized
    public void loadConversationTab(Map<Conversation, Message> conversationList) {
        Platform.runLater(() -> {
            tabTitle.setText("Chat");
        });
        if (conversationList == null) return;
        Comparator<Conversation> comparator = new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                return o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
            }
        };
        Map<Conversation, Message> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(conversationList);

        Platform.runLater(() -> {
            tabContent.getChildren().clear();

            sortedMap.forEach((conversation, message) -> {
                ConversationBox ins = ConversationBox.toConversationBox(conversation);
                String lastMessage = message.getMessage();
                if (message.getSenderId() == currUserId) {
                    lastMessage = "Bạn: " + lastMessage;
                }
                ins.setMessage(lastMessage);

                ins.setOnMouseClicked(e -> {
                    onConversationBoxClicked(ins);
                });
                tabContent.getChildren().add(0, ins);
            });
        });
    }

    private void onFriendBoxClicked(FriendBox ins) {
        if (ins.getStyleClass().contains("is-active")) return;
        activeStyleToggle(ins);
        chatTitle.setText(ins.getNameText());
        chatAvatar.setImage(ins.getAvatarImage());
        friendId = ins.getUserId();

        socketService.addRequest(
                ConversationRequest.builder()
                        .action(Action.GET_ONE_CONVERSATION)
                        .body(ins.getUserId())
                        .build()
        );
    }

    public void onConversationBoxClicked(ConversationBox ins) {
        if (ins.getStyleClass().contains("is-active")) return;
        activeStyleToggle(ins);
        chatTitle.setText(ins.getTitle());
        chatAvatar.setImage(ins.getAvatarImage());
        currConversation.set(ins.getConversation());
    }

    public void updateConversationBox(ConversationBox conversationBox, Message message) {
        if (conversationBox == null) return;
        if (!conversationBox.getStyleClass().contains("is-active")) {
            if (!conversationBox.getStyleClass().contains("unseen-message"))
                conversationBox.getStyleClass().add("unseen-message");
        }

        conversationBox.setTime(TimestampUtil.convertToString(message.getCreatedAt()));
        String lastMessage = message.getMessage();
        if (message.getSenderId() == currUserId) {
            lastMessage = "Bạn: " + lastMessage;
        }
        conversationBox.setMessage(lastMessage);
    }

    public void updateMessage(Message message, String senderName) {
        Platform.runLater(() -> {
            messageContainer.updateMessage(message, senderName);
            ConversationBox conversationBox = (ConversationBox) tabContent.lookup("#ConversationBox#" + message.getConversationId());
            if (!tabTitle.getText().equals("Chat")) return;
            tabContent.getChildren().remove(conversationBox);
            tabContent.getChildren().add(0, conversationBox);
            updateConversationBox(
                    conversationBox,
                    message
            );
        });
    }

    public void doSendMessage(String text) {
        Message message = new Message();
        message.setMessage(text);
        message.setSenderId(currUserId);

        Task queryConversation = new Task() {
            @Override
            protected Object call() throws Exception {
                if (currConversation.getValue() == null) {
                    socketService.addRequest(
                            ConversationRequest.builder()
                                    .action(Action.CREATE_CONVERSATION)
                                    .body(friendId)
                                    .build()
                    );
                }
                while (currConversation.getValue() == null) {
                    System.out.println("Wait result");
                }
                ;
                return null;
            }
        };

        queryConversation.setOnSucceeded(e -> {
            message.setConversationId(currConversation.getValue().getId());
            socketService.addRequest(
                    MessageRequest.builder()
                            .action(Action.SEND_MESSAGE)
                            .body(message)
                            .build()
            );
        });
        Thread th = new Thread(queryConversation);
        th.setDaemon(true);
        th.start();
    }


    public void loadInfo(InformationResponse res) {
        Class _class = res.get_class();
        if (_class == res.get_class()) {
            switch (res.getForAction()) {
                case GET_MEMBERS: {
                    if (groupMemberDialog == null) return;
                    groupMemberDialog.loadMemberList(res.getBody());
                    break;
                }
            }
        }
    }
}
