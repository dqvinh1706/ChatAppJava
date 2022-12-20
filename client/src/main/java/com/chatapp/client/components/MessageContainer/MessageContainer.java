package com.chatapp.client.components.MessageContainer;

import com.chatapp.commons.models.Message;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MessageContainer extends VBox {
    @Setter @Getter
    private Integer currUserId;
    private MessageBox currMessageBox = null;
    public MessageContainer() {
        super();
        initGui();
    }

    public void clear() {
        this.currMessageBox = null;
        this.getChildren().clear();
    }

    public void initGui() {
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setId("message-container");
        this.setSpacing(4);
    }

    public void loadMessages(List<Message> messageList) {
        if (messageList == null) return;
        messageList.forEach(message -> {
            Integer senderId = message.getSenderId();
            Platform.runLater(() -> {
                try {
                    if (currMessageBox == null || currMessageBox.getUserId() != senderId) {
                        currMessageBox = senderId.equals(currUserId)
                                ? new SendMessage(senderId) : new ReceiveMessage(senderId);
                        getChildren().add(0, currMessageBox);
                    }
                    currMessageBox.addTopMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void updateMessage(Message message) {
        if (currMessageBox != null) {
            MessageBox messageBox = (MessageBox) getChildren().get(getChildren().size() - 1);
            messageBox.addBottomMessage(message);
        }
    }
}
