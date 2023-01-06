package com.chatapp.client.components.UserTabs;

import com.chatapp.client.components.Avatar.Avatar;
import com.chatapp.client.components.ConversationBox.ConversationBox;
import com.chatapp.client.components.FriendBox.FriendBox;
import com.chatapp.client.components.FriendBox.PendingFriendBox;
import com.chatapp.client.components.FriendDialog.FriendDialog;
import com.chatapp.client.components.MessageContainer.MessageContainer;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.models.Message;
import com.chatapp.commons.models.User;

import com.chatapp.commons.request.ConversationRequest;
import com.chatapp.commons.request.FriendRequest;
import com.chatapp.commons.request.MessageRequest;
import com.chatapp.commons.response.ConversationResponse;
import com.chatapp.commons.utils.TimestampUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TabLayout extends GridPane {
    @Setter
    private int LIMIT = 5;
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
    private Label chatTitle, tabTitle;

    @FXML
    private TextField searchGlobal, searchMessageField, messageTextField;
    @FXML
    private VBox main, tabContent;

    private Avatar chatAvatar;
    protected final MessageContainer messageContainer = new MessageContainer();
    private final SimpleObjectProperty<Integer> currConversationId = new SimpleObjectProperty<>();
    protected int currUserId;
    protected Integer friendId = null;
    private UserSocketService socketService;

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

//        main.visibleProperty().bind(Bindings.isNotNull(currCon));

        currConversationId.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                messageContainer.clear();
                if (newValue == null) return;
                if (newValue.equals(oldValue)) return;
                offset = 0;
                sendChatHistoryRequest();
                System.out.println("Load");
                chatScrollPane.setVvalue(1.0f);
            });
        });

        chatScrollPane.setOnScroll(event -> {
            if (currConversationId.getValue() == null) return;
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

    public void setCurrConversationId(Conversation con) {
        this.currConversationId.set(con == null ? null : con.getId());
    }

    public void sendChatHistoryRequest() {
        Properties body = new Properties();
        body.put("conId", currConversationId.getValue());
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
    void onAddFriendByUsername(ActionEvent event) throws IOException {
        friendDialog = new FriendDialog();
        friendDialog.show();

    }

    @FXML
    void onCreateGrByUsername(ActionEvent event) {

    }

    @FXML
    private void onDeleteConversation(ActionEvent event) {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Xoá cuộc trò chuyện?");
        confirmDelete.setHeaderText(null);
        confirmDelete.setContentText("Bạn không thể hoàn tác sau khi xoá cuộc trò chuyện này.");
        Optional<ButtonType> option = confirmDelete.showAndWait();
        if (option.get() == ButtonType.OK) {
            if (currConversationId.getValue() == null) {
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
                            .body(currConversationId.getValue())
                            .build()
            );

            // Update gui
            Platform.runLater(() ->
                    tabContent.getChildren().remove(tabContent.lookup("#ConversationBox#" + currConversationId.getValue()))
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
//            loadConversationTab();
        }
//        else {
//            loadFriendTab();
//        }
    }

    @Synchronized
    public void loadChatHistory(List<Message> messageList) {
        if (messageList == null) return;
        offset += LIMIT;
        messageContainer.loadMessages(messageList);
    }

    @Synchronized
    public void loadFriendTab(String title, List<User> users) {
        Platform.runLater(() -> {
            tabContent.getChildren().clear();
            if (users == null) return;
            tabTitle.setText(title);
            if (title.equals("Lời mời kết bạn")) {
                loadPendingFriendsTab(users);
                return;
            }
            users.forEach(friend -> {
                System.out.println("Pendding");
                FriendBox ins = FriendBox.toFriendBox(friend);
                ins.setOnMouseClicked(e -> {
                    onFriendBoxClicked(ins);
                });
                tabContent.getChildren().add(0, ins);
            });

        });
    }

    private void loadPendingFriendsTab(List<User> users) {
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
            tabTitle.setText("Chat");
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
        currConversationId.set(ins.getConversationId());
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

    public void updateMessage(Message message) {
        Platform.runLater(() -> {
            messageContainer.updateMessage(message);
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
                if (currConversationId.getValue() == null) {
                    socketService.addRequest(
                            ConversationRequest.builder()
                                    .action(Action.CREATE_CONVERSATION)
                                    .body(friendId)
                                    .build()
                    );
                }
                while (currConversationId.getValue() == null) {
                    System.out.println("Wait result");
                }
                ;
                return null;
            }
        };

        queryConversation.setOnSucceeded(e -> {
            message.setConversationId(currConversationId.getValue());
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
}