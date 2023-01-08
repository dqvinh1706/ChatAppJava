package com.chatapp.client.components.MessageContainer;

import com.chatapp.commons.models.Message;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MessageContainer extends VBox {
    @Setter
    @Getter
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

    public void loadMessages(List<Message> messageList, List<String> senderNames) {
        if (messageList == null) return;
        for (Message message : messageList) {
            Platform.runLater(() -> {
                Integer senderId = message.getSenderId();
                try {

                    if (currMessageBox == null || currMessageBox.getUserId() != senderId) {
                        if (senderId.equals(currUserId))
                            currMessageBox = new SendMessage(senderId);
                        else {
                            currMessageBox = new ReceiveMessage(senderId, senderNames.get(senderNames.size() - 1));
                            if (!senderNames.isEmpty())
                                senderNames.remove(0);
                        }
                        getChildren().add(0, currMessageBox);

                    }
                    currMessageBox.addTopMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void updateMessage(Message message, String senderName) {
        if (currMessageBox != null) {
            MessageBox messageBox = (MessageBox) getChildren().get(getChildren().size() - 1);
            if (message.getSenderId() != messageBox.getUserId()) {
                MessageBox newMessageBox = null;
                if (message.getSenderId() == currUserId) {
                    newMessageBox = new SendMessage(message.getSenderId());
                }
                else {
                    newMessageBox = new ReceiveMessage(message.getSenderId(), senderName);
                }
                newMessageBox.addBottomMessage(message);
                getChildren().add(newMessageBox);
            } else {
                messageBox.addBottomMessage(message);
            }
        }
    }
}
